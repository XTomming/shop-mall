Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const require_runtime = require("../../../utils/vue/props/runtime.js");
let _vue_shared = require("@vue/shared");
//#region ../../packages/components/calendar/src/select-controller.ts
/**
*  @deprecated Removed after 3.0.0, Use `SelectControllerProps` instead.
*/
const selectControllerProps = require_runtime.buildProps({
	date: {
		type: require_runtime.definePropType(Object),
		required: true
	},
	formatter: { type: require_runtime.definePropType(Function) }
});
const selectControllerEmits = { "date-change": (date) => (0, _vue_shared.isObject)(date) || (0, _vue_shared.isString)(date) };
//#endregion
exports.selectControllerEmits = selectControllerEmits;
exports.selectControllerProps = selectControllerProps;

//# sourceMappingURL=select-controller.js.map