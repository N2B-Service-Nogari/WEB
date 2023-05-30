export const toDoubleDigit = (num: number | string) => num.toString().length < 2 ? `0${num}` : num
