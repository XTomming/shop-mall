Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const require_runtime = require("../../utils/vue/props/runtime.js");
let lodash_unified = require("lodash-unified");
//#region ../../packages/hooks/use-aria/index.ts
/**
* @deprecated Removed after 3.0.0, Use `AriaProps` instead.
*/
const ariaProps = require_runtime.buildProps({
	/**
	* @description native `aria-label` attribute
	*/
	ariaLabel: String,
	/**
	* @description native `aria-orientation` attribute
	*/
	ariaOrientation: {
		type: String,
		values: [
			"horizontal",
			"vertical",
			"undefined"
		]
	},
	/**
	* @description native `aria-controls` attribute
	*/
	ariaControls: String
});
const useAriaProps = (arias) => {
	return (0, lodash_unified.pick)(ariaProps, arias);
};
//#endregion
exports.ariaProps = ariaProps;
exports.useAriaProps = useAriaProps;

//# sourceMappingURL=index.js.map