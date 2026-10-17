import { isNumber } from "../../../utils/types.mjs";
//#region ../../packages/components/countdown/src/utils.ts
const timeUnits = [
	["Y", 31536e6],
	["M", 2592e6],
	["D", 864e5],
	["H", 36e5],
	["m", 6e4],
	["s", 1e3],
	["S", 1]
];
const getTime = (value) => {
	return isNumber(value) ? new Date(value).getTime() : value.valueOf();
};
const formatTime = (timestamp, format) => {
	let timeLeft = timestamp;
	return timeUnits.reduce((current, [name, unit]) => {
		const replaceRegex = new RegExp(`${name}+(?![^\\[\\]]*\\])`, "g");
		if (replaceRegex.test(current)) {
			const value = Math.floor(timeLeft / unit);
			timeLeft -= value * unit;
			return current.replace(replaceRegex, (match) => String(value).padStart(match.length, "0"));
		}
		return current;
	}, format).replace(/\[([^\]]*)]/g, "$1");
};
//#endregion
export { formatTime, getTime };

//# sourceMappingURL=utils.mjs.map