package com.aicrm.kernel.page;

import java.util.List;

public record PageResult<T>(List<T> records, long page, long size, long total) {
}
