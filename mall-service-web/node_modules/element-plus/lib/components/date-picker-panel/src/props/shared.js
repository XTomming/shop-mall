Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const require_date = require("../../../../constants/date.js");
const require_runtime = require("../../../../utils/vue/props/runtime.js");
let _vue_shared = require("@vue/shared");
//#region ../../packages/components/date-picker-panel/src/props/shared.ts
const selectionModes = [
	"date",
	"dates",
	"year",
	"years",
	"month",
	"months",
	"week",
	"quarter",
	"quarters",
	"range"
];
const datePickerSharedProps = require_runtime.buildProps({
	cellClassName: { type: require_runtime.definePropType(Function) },
	disabledDate: { type: require_runtime.definePropType(Function) },
	date: {
		type: require_runtime.definePropType(Object),
		required: true
	},
	minDate: { type: require_runtime.definePropType(Object) },
	maxDate: { type: require_runtime.definePropType(Object) },
	parsedValue: { type: require_runtime.definePropType([Object, Array]) },
	rangeState: {
		type: require_runtime.definePropType(Object),
		default: () => ({
			endDate: null,
			selecting: false
		})
	},
	disabled: Boolean
});
const panelSharedProps = require_runtime.buildProps({
	type: {
		type: require_runtime.definePropType(String),
		required: true,
		values: require_date.datePickTypes
	},
	dateFormat: String,
	timeFormat: String,
	showNow: {
		type: Boolean,
		default: true
	},
	showConfirm: Boolean,
	showFooter: {
		type: Boolean,
		default: true
	},
	showWeekNumber: Boolean,
	border: Boolean,
	disabled: Boolean,
	editable: {
		type: Boolean,
		default: true
	}
});
const panelRangeSharedProps = require_runtime.buildProps({
	unlinkPanels: Boolean,
	visible: {
		type: Boolean,
		default: true
	},
	showConfirm: Boolean,
	showFooter: {
		type: Boolean,
		default: true
	},
	border: Boolean,
	disabled: Boolean,
	parsedValue: { type: require_runtime.definePropType(Array) },
	singlePanel: Boolean
});
const selectionModeWithDefault = (mode) => {
	return {
		type: String,
		values: selectionModes,
		default: mode
	};
};
const rangePickerSharedEmits = { pick: (range) => (0, _vue_shared.isArray)(range) };
//#endregion
exports.datePickerSharedProps = datePickerSharedProps;
exports.panelRangeSharedProps = panelRangeSharedProps;
exports.panelSharedProps = panelSharedProps;
exports.rangePickerSharedEmits = rangePickerSharedEmits;
exports.selectionModeWithDefault = selectionModeWithDefault;

//# sourceMappingURL=shared.js.map