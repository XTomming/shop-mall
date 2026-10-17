import { isArray, isString } from "../../../utils/types.mjs";
import { rangeArr } from "../../time-picker/src/utils.mjs";
import dayjs from "dayjs";
//#region ../../packages/components/date-picker-panel/src/utils.ts
const isValidRange = (range) => {
	if (!isArray(range)) return false;
	const [left, right] = range;
	return dayjs.isDayjs(left) && dayjs.isDayjs(right) && dayjs(left).isValid() && dayjs(right).isValid() && left.isSameOrBefore(right);
};
const getDefaultValue = (defaultValue, { lang, step = 1, unit, unlinkPanels }) => {
	let start;
	if (isArray(defaultValue)) {
		let [left, right] = defaultValue.map((d) => dayjs(d).locale(lang));
		if (!unlinkPanels) right = left.add(step, unit);
		return [left, right];
	} else if (defaultValue) start = dayjs(defaultValue);
	else start = dayjs();
	start = start.locale(lang);
	return [start, start.add(step, unit)];
};
const buildPickerTable = (dimension, rows, { columnIndexOffset, startDate, nextEndDate, now, unit, relativeDateGetter, setCellMetadata, setRowMetadata }) => {
	for (let rowIndex = 0; rowIndex < dimension.row; rowIndex++) {
		const row = rows[rowIndex];
		for (let columnIndex = 0; columnIndex < dimension.column; columnIndex++) {
			let cell = row[columnIndex + columnIndexOffset];
			if (!cell) cell = {
				row: rowIndex,
				column: columnIndex,
				type: "normal",
				inRange: false,
				start: false,
				end: false
			};
			const nextStartDate = relativeDateGetter(rowIndex * dimension.column + columnIndex);
			cell.dayjs = nextStartDate;
			cell.date = nextStartDate.toDate();
			cell.timestamp = nextStartDate.valueOf();
			cell.type = "normal";
			cell.inRange = !!(startDate && nextStartDate.isSameOrAfter(startDate, unit) && nextEndDate && nextStartDate.isSameOrBefore(nextEndDate, unit)) || !!(startDate && nextStartDate.isSameOrBefore(startDate, unit) && nextEndDate && nextStartDate.isSameOrAfter(nextEndDate, unit));
			if (startDate?.isSameOrAfter(nextEndDate)) {
				cell.start = !!nextEndDate && nextStartDate.isSame(nextEndDate, unit);
				cell.end = startDate && nextStartDate.isSame(startDate, unit);
			} else {
				cell.start = !!startDate && nextStartDate.isSame(startDate, unit);
				cell.end = !!nextEndDate && nextStartDate.isSame(nextEndDate, unit);
			}
			if (nextStartDate.isSame(now, unit)) cell.type = "today";
			setCellMetadata?.(cell, {
				rowIndex,
				columnIndex
			});
			row[columnIndex + columnIndexOffset] = cell;
		}
		setRowMetadata?.(row);
	}
};
const datesInMonth = (date, year, month, lang) => {
	const firstDay = dayjs().locale(lang).startOf("month").month(month).year(year).hour(date.hour()).minute(date.minute()).second(date.second());
	const numOfDays = firstDay.daysInMonth();
	return rangeArr(numOfDays).map((n) => firstDay.add(n, "day").toDate());
};
const datesInQuarter = (date, year, quarter, lang) => {
	const firstMonth = quarter * 3;
	return [
		0,
		1,
		2
	].reduce((acc, i) => acc.concat(datesInMonth(date, year, firstMonth + i, lang)), []);
};
const isQuarterFullyDisabled = (value, lang, disabledDate) => {
	if (!disabledDate) return false;
	return datesInQuarter(value, value.year(), value.quarter() - 1, lang).every(disabledDate);
};
const isSelectableQuarterDate = (value, lang, disabledDate) => {
	return (disabledDate ? !disabledDate(value.toDate()) : true) && !isQuarterFullyDisabled(value, lang, disabledDate);
};
const getValidDateOfMonth = (date, year, month, lang, disabledDate) => {
	const _value = dayjs().year(year).month(month).startOf("month").hour(date.hour()).minute(date.minute()).second(date.second());
	const _date = datesInMonth(date, year, month, lang).find((date) => {
		return !disabledDate?.(date);
	});
	if (_date) return dayjs(_date).locale(lang);
	return _value.locale(lang);
};
const getValidDateOfQuarter = (date, year, quarter, lang, disabledDate) => {
	const firstMonth = quarter * 3;
	for (let i = 0; i < 3; i++) {
		const month = firstMonth + i;
		if (!datesInMonth(date, year, month, lang).every((d) => disabledDate?.(d))) return getValidDateOfMonth(date, year, month, lang, disabledDate);
	}
	return dayjs().year(year).month(firstMonth).startOf("month").hour(date.hour()).minute(date.minute()).second(date.second()).locale(lang);
};
const normalizeQuarterDate = (value, lang, disabledDate) => {
	if (!dayjs.isDayjs(value) || !value.isValid()) return value;
	return getValidDateOfQuarter(value.startOf("day"), value.year(), value.quarter() - 1, lang, disabledDate);
};
const getValidDateOfYear = (value, lang, disabledDate) => {
	const year = value.year();
	if (!disabledDate?.(value.toDate())) return value.locale(lang);
	const month = value.month();
	if (!datesInMonth(value, year, month, lang).every(disabledDate)) return getValidDateOfMonth(value, year, month, lang, disabledDate);
	for (let i = 0; i < 12; i++) if (!datesInMonth(value, year, i, lang).every(disabledDate)) return getValidDateOfMonth(value, year, i, lang, disabledDate);
	return value;
};
const correctlyParseUserInput = (value, format, lang) => {
	if (isArray(value)) return value.map((v) => correctlyParseUserInput(v, format, lang));
	if (isString(value)) {
		let dayjsValue = dayjs(value, format);
		if (!dayjsValue.isValid()) dayjsValue = dayjs(value);
		if (!dayjsValue.isValid()) return dayjsValue;
		const formatValue = dayjsValue.format(format);
		if (!dayjs(formatValue, format, true).isValid()) return dayjs("");
		return dayjs(formatValue, format).locale(lang);
	}
	return dayjs(value, format).locale(lang);
};
//#endregion
export { buildPickerTable, correctlyParseUserInput, datesInMonth, datesInQuarter, getDefaultValue, getValidDateOfMonth, getValidDateOfQuarter, getValidDateOfYear, isQuarterFullyDisabled, isSelectableQuarterDate, isValidRange, normalizeQuarterDate };

//# sourceMappingURL=utils.mjs.map