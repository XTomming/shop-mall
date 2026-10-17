const require_runtime = require("../../../../_virtual/_rolldown/runtime.js");
const require_style = require("../../../../utils/dom/style.js");
const require_arrays = require("../../../../utils/arrays.js");
const require_index = require("../../../../hooks/use-locale/index.js");
const require_index$1 = require("../../../../hooks/use-namespace/index.js");
const require_utils = require("../utils.js");
const require_basic_cell_render = require("./basic-cell-render.js");
const require_basic_quarter_table = require("../props/basic-quarter-table.js");
let vue = require("vue");
let dayjs = require("dayjs");
dayjs = require_runtime.__toESM(dayjs);
//#region ../../packages/components/date-picker-panel/src/date-picker-com/basic-quarter-table.vue?vue&type=script&setup=true&lang.ts
const _hoisted_1 = ["aria-label"];
const _hoisted_2 = [
	"aria-selected",
	"aria-label",
	"tabindex",
	"onKeydown"
];
const QUARTER_UNIT = "quarter";
const COL_COUNT = 4;
var basic_quarter_table_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ (0, vue.defineComponent)({
	__name: "basic-quarter-table",
	props: require_basic_quarter_table.basicQuarterTableProps,
	emits: [
		"changerange",
		"pick",
		"select"
	],
	setup(__props, { expose: __expose, emit: __emit }) {
		const props = __props;
		const emit = __emit;
		const ns = require_index$1.useNamespace("quarter-table");
		const { t, lang } = require_index.useLocale();
		const tbodyRef = (0, vue.ref)();
		const currentCellRef = (0, vue.ref)();
		const tableColumns = (0, vue.ref)([]);
		let lastQuarter;
		const resolveQuarterDate = (quarter) => require_utils.normalizeQuarterDate(props.date.startOf("year").add(quarter, "quarter"), lang.value, props.disabledDate);
		const isSelectedCell = (cell) => {
			const year = props.date.year();
			const quarter = cell.text;
			return require_arrays.castArray(props.date).some((date) => date.year() === year && date.quarter() - 1 === quarter);
		};
		const createQuarterCell = (column) => ({
			row: 0,
			column,
			type: "normal",
			inRange: false,
			start: false,
			end: false,
			text: -1,
			disabled: false,
			isSelected: false,
			customClass: void 0,
			date: void 0,
			dayjs: void 0,
			isCurrent: void 0,
			selected: void 0,
			renderText: void 0,
			timestamp: void 0
		});
		const resolveRangeEndDate = () => props.rangeState.endDate || props.maxDate || props.rangeState.selecting && props.minDate || null;
		const isQuarterDisabled = (quarter) => props.disabled || (props.disabledDate ? require_utils.datesInQuarter(props.date, props.date.year(), quarter, lang.value).every(props.disabledDate) : false);
		const updateQuarterCell = (cell, col, now, calEndDate) => {
			cell.type = "normal";
			const calTime = props.date.startOf("year").add(col, "quarter");
			cell.inRange = !!(props.minDate && calTime.isSameOrAfter(props.minDate, QUARTER_UNIT) && calEndDate && calTime.isSameOrBefore(calEndDate, QUARTER_UNIT)) || !!(props.minDate && calTime.isSameOrBefore(props.minDate, QUARTER_UNIT) && calEndDate && calTime.isSameOrAfter(calEndDate, QUARTER_UNIT));
			if (props.minDate?.isSameOrAfter(calEndDate, QUARTER_UNIT)) {
				cell.start = !!(calEndDate && calTime.isSame(calEndDate, QUARTER_UNIT));
				cell.end = !!(props.minDate && calTime.isSame(props.minDate, QUARTER_UNIT));
			} else {
				cell.start = !!(props.minDate && calTime.isSame(props.minDate, QUARTER_UNIT));
				cell.end = !!(calEndDate && calTime.isSame(calEndDate, QUARTER_UNIT));
			}
			if (now.isSame(calTime)) cell.type = "today";
			const cellDate = calTime.toDate();
			cell.text = col;
			cell.disabled = isQuarterDisabled(col);
			cell.date = cellDate;
			cell.customClass = props.cellClassName?.(cellDate);
			cell.dayjs = calTime;
			cell.timestamp = calTime.valueOf();
			cell.isSelected = isSelectedCell(cell);
		};
		const columns = (0, vue.computed)(() => {
			const cells = tableColumns.value;
			const now = (0, dayjs.default)().locale(lang.value).startOf("quarter");
			const calEndDate = resolveRangeEndDate();
			for (let col = 0; col < COL_COUNT; col++) {
				const cell = cells[col] ||= createQuarterCell(col);
				updateQuarterCell(cell, col, now, calEndDate);
			}
			return cells;
		});
		const focus = () => {
			currentCellRef.value?.focus();
		};
		const getCellStyle = (cell) => {
			const style = {};
			const year = props.date.year();
			const today = (0, dayjs.default)().locale(lang.value);
			const quarter = cell.text;
			style.disabled = isQuarterDisabled(quarter);
			style.current = require_arrays.castArray(props.parsedValue).some((date) => dayjs.default.isDayjs(date) && date.year() === year && date.quarter() - 1 === quarter);
			style.today = today.year() === year && today.quarter() - 1 === quarter;
			if (cell.customClass) style[cell.customClass] = true;
			if (cell.inRange) {
				style["in-range"] = true;
				if (cell.start) style["start-date"] = true;
				if (cell.end) style["end-date"] = true;
			}
			return style;
		};
		const handleRangeHover = (event) => {
			if (!props.rangeState.selecting) return;
			const target = event.target?.closest("td");
			if (target?.tagName !== "TD") return;
			const quarter = target.cellIndex;
			if (columns.value[quarter].disabled) return;
			if (quarter !== lastQuarter) {
				lastQuarter = quarter;
				emit("changerange", {
					selecting: true,
					endDate: props.date.startOf("year").add(quarter, "quarter")
				});
			}
		};
		const handleTableMouseLeave = () => {
			lastQuarter = void 0;
		};
		const handleQuarterTableClick = (event) => {
			if (props.disabled) return;
			const target = event.target?.closest("td");
			if (target?.tagName !== "TD" || require_style.hasClass(target, "disabled")) return;
			const quarter = target.cellIndex;
			if (props.selectionMode === "range") {
				const newDate = resolveQuarterDate(quarter);
				if (!props.rangeState.selecting) {
					emit("pick", {
						minDate: newDate,
						maxDate: null
					});
					emit("select", true);
					return;
				}
				if (props.minDate && newDate >= props.minDate) emit("pick", {
					minDate: props.minDate,
					maxDate: newDate
				});
				else emit("pick", {
					minDate: newDate,
					maxDate: props.minDate
				});
				emit("select", false);
				return;
			}
			if (props.selectionMode === "quarters") {
				const newQuarter = resolveQuarterDate(quarter);
				const newValue = require_style.hasClass(target, "current") ? require_arrays.castArray(props.parsedValue).filter((d) => d?.year() !== newQuarter.year() || d.quarter() - 1 !== quarter) : require_arrays.castArray(props.parsedValue).concat([newQuarter]);
				emit("pick", newValue);
				return;
			}
			emit("pick", quarter);
		};
		(0, vue.watch)(() => props.date, async () => {
			if (tbodyRef.value?.contains(document.activeElement)) {
				await (0, vue.nextTick)();
				currentCellRef.value?.focus();
			}
		});
		__expose({ 
		/**
		* @description focus current cell
		*/
focus });
		return (_ctx, _cache) => {
			return (0, vue.openBlock)(), (0, vue.createElementBlock)("table", {
				role: "grid",
				"aria-label": (0, vue.unref)(t)("el.datepicker.quarterTablePrompt"),
				class: (0, vue.normalizeClass)((0, vue.unref)(ns).b()),
				onClick: handleQuarterTableClick,
				onMousemove: handleRangeHover,
				onMouseleave: handleTableMouseLeave
			}, [(0, vue.createElementVNode)("tbody", {
				ref_key: "tbodyRef",
				ref: tbodyRef
			}, [(0, vue.createElementVNode)("tr", null, [((0, vue.openBlock)(true), (0, vue.createElementBlock)(vue.Fragment, null, (0, vue.renderList)(columns.value, (cell, key) => {
				return (0, vue.openBlock)(), (0, vue.createElementBlock)("td", {
					key,
					ref_for: true,
					ref: (el) => cell.isSelected && (currentCellRef.value = el),
					class: (0, vue.normalizeClass)(["available", getCellStyle(cell)]),
					"aria-selected": !!cell.isSelected,
					"aria-label": `Q${cell.text + 1}`,
					tabindex: cell.isSelected ? 0 : -1,
					onKeydown: [(0, vue.withKeys)((0, vue.withModifiers)(handleQuarterTableClick, ["prevent", "stop"]), ["space"]), (0, vue.withKeys)((0, vue.withModifiers)(handleQuarterTableClick, ["prevent", "stop"]), ["enter"])]
				}, [(0, vue.createVNode)((0, vue.unref)(require_basic_cell_render.default), { cell: {
					...cell,
					renderText: `Q${cell.text + 1}`
				} }, null, 8, ["cell"])], 42, _hoisted_2);
			}), 128))])], 512)], 42, _hoisted_1);
		};
	}
});
//#endregion
exports.default = basic_quarter_table_vue_vue_type_script_setup_true_lang_default;

//# sourceMappingURL=basic-quarter-table.vue_vue_type_script_setup_true_lang.js.map