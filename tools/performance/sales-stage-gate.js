import http from 'k6/http';
import { check, fail } from 'k6';
import { Trend } from 'k6/metrics';

const baseUrl = required('PERF_BASE_URL').replace(/\/$/, '');
const token = required('PERF_BEARER_TOKEN');
const leadId = required('PERF_FOLLOW_UP_LEAD_ID');
const listVus = integerEnv('PERF_LIST_VUS', 20);
const listDuration = __ENV.PERF_LIST_DURATION || '2m';
const commandIterations = integerEnv('PERF_COMMAND_ITERATIONS', 100);

const listLatency = new Trend('crm_common_list_latency_ms', true);
const commandLatency = new Trend('crm_business_command_latency_ms', true);

export const options = {
  discardResponseBodies: false,
  scenarios: {
    common_lists: {
      executor: 'constant-vus',
      exec: 'commonLists',
      vus: listVus,
      duration: listDuration,
      gracefulStop: '15s',
    },
    follow_up_command: {
      executor: 'per-vu-iterations',
      exec: 'followUp',
      vus: 1,
      iterations: commandIterations,
      maxDuration: '10m',
    },
  },
  thresholds: {
    checks: ['rate==1'],
    crm_common_list_latency_ms: ['p(95)<300'],
    crm_business_command_latency_ms: ['p(95)<500'],
  },
};

export function setup() {
  const response = http.get(`${baseUrl}/api/v1/leads/${leadId}`, requestParams('lead-detail'));
  if (!check(response, { 'configured follow-up lead is readable': (result) => result.status === 200 })) {
    fail(`The configured lead ${leadId} is not readable by the supplied performance-test token.`);
  }
  const lead = response.json('data');
  if (!lead || typeof lead.version !== 'number') {
    fail('The lead detail response does not contain a numeric data.version field.');
  }
}

export function commonLists() {
  const page = ((__VU + __ITER) % 20) + 1;
  const response = http.get(`${baseUrl}/api/v1/leads/private?page=${page}&size=20`, requestParams('lead-private-list'));
  listLatency.add(response.timings.duration);
  check(response, { 'private lead list succeeds': (result) => result.status === 200 });
}

export function followUp() {
  const detail = http.get(`${baseUrl}/api/v1/leads/${leadId}`, requestParams('lead-detail-before-follow-up'));
  if (!check(detail, { 'lead detail before follow-up succeeds': (result) => result.status === 200 })) {
    return;
  }
  const lead = detail.json('data');
  if (!lead || typeof lead.version !== 'number') {
    fail('The configured lead response does not contain a numeric version.');
  }

  const response = http.post(
    `${baseUrl}/api/v1/leads/${leadId}/follow-ups`,
    JSON.stringify({
      version: lead.version,
      channel: 'PHONE',
      content: `Performance stage gate ${__VU}-${__ITER}`,
      nextFollowUpAt: null,
    }),
    requestParams('lead-follow-up'),
  );
  commandLatency.add(response.timings.duration);
  check(response, { 'lead follow-up command succeeds': (result) => result.status === 200 });
}

function requestParams(name) {
  return {
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
    tags: { name },
  };
}

function required(name) {
  const value = __ENV[name];
  if (!value) {
    fail(`${name} must be supplied.`);
  }
  return value;
}

function integerEnv(name, fallback) {
  const value = __ENV[name];
  if (!value) return fallback;
  const parsed = Number(value);
  if (!Number.isInteger(parsed) || parsed < 1) {
    fail(`${name} must be a positive integer.`);
  }
  return parsed;
}
