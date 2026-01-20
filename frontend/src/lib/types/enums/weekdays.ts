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

export enum TimeSlots {
    SIX = '6:00',
    SIX_HALF = '6:30',
    SEVEN = '7:00',
    SEVEN_HALF = '7:30',
    EIGHT = '8:00',
    EIGHT_HALF = '8:30',
    NINE = '9:00',
    NINE_HALF = '9:30',
    TEN = '10:00',
    TEN_HALF = '10:30',
    ELEVEN = '11:00',
    ELEVEN_HALF = '11:30',
    TWELVE = '12:00',
    TWELVE_HALF = '12:30',
    THIRTEEN = '13:00',
    THIRTEEN_HALF = '13:30',
    FOURTEEN = '14:00',
    FOURTEEN_HALF = '14:30',
    FIFTEEN = '15:00',
    FIFTEEN_HALF = '15:30',
    SIXTEEN = '16:00',
    SIXTEEN_HALF = '16:30',
    SEVENTEEN = '17:00',
    SEVENTEEN_HALF = '17:30',
    EIGHTEEN = '18:00',
    EIGHTEEN_HALF = '18:30',
    NINETEEN = '19:00',
    NINETEEN_HALF = '19:30',
    TWENTY = '20:00',
    TWENTY_HALF = '20:30',
    TWENTY_ONE = '21:00',
    TWENTY_ONE_HALF = '21:30',
    TWENTY_TWO = '22:00'
};

export enum Durations {
    FIFTEEN_MINUTES = '15',
    THIRTY_MINUTES = '30',
    FORTY_FIVE_MINUTES = '45',
    SIXTY_MINUTES = '60'
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

export const getWeekdayShortLabel = (day: Weekdays) => {
    switch(day) {
        case Weekdays.MONDAY: return m['weekdays_short.mon']();
        case Weekdays.TUESDAY: return m['weekdays_short.tue']();
        case Weekdays.WEDNESDAY: return m['weekdays_short.wed']();
        case Weekdays.THURSDAY: return m['weekdays_short.thu']();
        case Weekdays.FRIDAY: return m['weekdays_short.fri']();
        case Weekdays.SATURDAY: return m['weekdays_short.sat']();
        case Weekdays.SUNDAY: return m['weekdays_short.sun']();
    }
};