import { m } from "$lib/paraglide/messages";

export enum Weekdays {
    MONDAY = 'MONDAY',
    TUESDAY = 'TUESDAY',
    WEDNESDAY = 'WEDNESDAY',
    THURSDAY = 'THURSDAY',
    FRIDAY = 'FRIDAY',
    SATURDAY = 'SATURDAY',
    SUNDAY = 'SUNDAY'
};

export const getWeekdayLabel = (day: Weekdays) => {
    switch(day) {
        case Weekdays.MONDAY: return m['filters.weekdays.monday']();
        case Weekdays.TUESDAY: return m['filters.weekdays.tuesday']();
        case Weekdays.WEDNESDAY: return m['filters.weekdays.wednesday']();
        case Weekdays.THURSDAY: return m['filters.weekdays.thursday']();
        case Weekdays.FRIDAY: return m['filters.weekdays.friday']();
        case Weekdays.SATURDAY: return m['filters.weekdays.saturday']();
        case Weekdays.SUNDAY: return m['filters.weekdays.sunday']();
    }
};