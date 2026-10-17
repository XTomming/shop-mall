import { buildProps } from "../../../../utils/vue/props/runtime.mjs";
import { panelRangeSharedProps } from "./shared.mjs";
//#region ../../packages/components/date-picker-panel/src/props/panel-quarter-range.ts
const panelQuarterRangeProps = buildProps({ ...panelRangeSharedProps });
const panelQuarterRangeEmits = [
	"pick",
	"set-picker-option",
	"calendar-change"
];
//#endregion
export { panelQuarterRangeEmits, panelQuarterRangeProps };

//# sourceMappingURL=panel-quarter-range.mjs.map