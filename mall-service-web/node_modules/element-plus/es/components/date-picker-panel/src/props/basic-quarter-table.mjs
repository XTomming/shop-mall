import { buildProps } from "../../../../utils/vue/props/runtime.mjs";
import { datePickerSharedProps, selectionModeWithDefault } from "./shared.mjs";
//#region ../../packages/components/date-picker-panel/src/props/basic-quarter-table.ts
const basicQuarterTableProps = buildProps({
	...datePickerSharedProps,
	selectionMode: selectionModeWithDefault("quarter")
});
//#endregion
export { basicQuarterTableProps };

//# sourceMappingURL=basic-quarter-table.mjs.map