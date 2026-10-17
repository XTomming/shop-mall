Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const require_types = require("../../../utils/types.js");
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
	return require_types.isNumber(value) ? new Date(value).getTime() : value.valueOf();
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
exports.formatTime = formatTime;
exports.getTime = getTime;

//# sourceMappingURL=utils.js.map