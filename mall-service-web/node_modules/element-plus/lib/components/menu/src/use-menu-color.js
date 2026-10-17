Object.defineProperties(exports, {
	__esModule: { value: true },
	[Symbol.toStringTag]: { value: "Module" }
});
let vue = require("vue");
let _ctrl_tinycolor = require("@ctrl/tinycolor");
//#region ../../packages/components/menu/src/use-menu-color.ts
function useMenuColor(props) {
	return (0, vue.computed)(() => {
		const color = props.backgroundColor;
		return color ? new _ctrl_tinycolor.TinyColor(color).shade(20).toString() : "";
	});
}
//#endregion
exports.default = useMenuColor;

//# sourceMappingURL=use-menu-color.js.map