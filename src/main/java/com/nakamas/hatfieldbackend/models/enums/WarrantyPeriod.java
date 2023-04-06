package com.nakamas.hatfieldbackend.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Period;

@Getter
@AllArgsConstructor
public enum WarrantyPeriod {
    NONE(Period.ZERO),
    ONE_DAY(Period.ofDays(1)),
    THREE_DAYS(Period.ofDays(3)),
    ONE_WEEK(Period.ofWeeks(1)),
    TWO_WEEKS(Period.ofWeeks(2)),
    ONE_MONTH(Period.ofDays(30)),
    THREE_MONTHS(Period.ofMonths(3)),
    SIX_MONTHS(Period.ofMonths(6)),
    ONE_YEAR(Period.ofYears(1)),
    TWO_YEARS(Period.ofYears(2));

    private final Period period;
}
