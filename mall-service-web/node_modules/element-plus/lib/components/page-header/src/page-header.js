Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const require_runtime = require("../../../utils/vue/props/runtime.js");
const require_icon = require("../../../utils/vue/icon.js");
let _element_plus_icons_vue = require("@element-plus/icons-vue");
//#region ../../packages/components/page-header/src/page-header.ts
/**
* @deprecated Removed after 3.0.0, Use `PageHeaderProps` instead.
*/
const pageHeaderProps = require_runtime.buildProps({
	/**
	* @description icon component of page header
	*/
	icon: {
		type: require_icon.iconPropType,
		default: () => _element_plus_icons_vue.Back
	},
	/**
	* @description main title of page header
	*/
	title: String,
	/**
	* @description content of page header
	*/
	content: {
		type: String,
		default: ""
	}
});
const pageHeaderEmits = { back: () => true };
//#endregion
exports.pageHeaderEmits = pageHeaderEmits;
exports.pageHeaderProps = pageHeaderProps;

//# sourceMappingURL=page-header.js.map