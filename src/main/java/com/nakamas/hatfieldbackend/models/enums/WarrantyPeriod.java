package com.nakamas.hatfieldbackend.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

@Getter
@AllArgsConstructor
public enum WarrantyPeriod {
    WEEK(Period.ofWeeks(1)),
    TWO_WEEKS(Period.ofWeeks(2)),
    THIRTY_DAYS(Period.ofDays(30)),
    THREE_MONTHS(Period.ofMonths(3) ),
    SIX_MONTHS(Period.ofMonths(6)),
    YEAR(Period.ofYears(1));

    private final Period period;
}
