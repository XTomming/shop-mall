Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const require_runtime = require("../../../utils/vue/props/runtime.js");
let _vue_shared = require("@vue/shared");
//#region ../../packages/components/mention/src/mention-dropdown.ts
/**
* @deprecated Removed after 3.0.0, Use `MentionDropdownProps` instead.
*/
const mentionDropdownProps = require_runtime.buildProps({
	options: {
		type: require_runtime.definePropType(Array),
		default: () => []
	},
	loading: Boolean,
	disabled: Boolean,
	contentId: String,
	ariaLabel: String
});
const mentionDropdownEmits = { select: (option) => (0, _vue_shared.isString)(option.value) };
//#endregion
exports.mentionDropdownEmits = mentionDropdownEmits;
exports.mentionDropdownProps = mentionDropdownProps;

//# sourceMappingURL=mention-dropdown.js.map