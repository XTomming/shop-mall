const require_types = require("../../../utils/types.js");
const require_raf = require("../../../utils/raf.js");
const require_strings = require("../../../utils/strings.js");
const require_error = require("../../../utils/error.js");
const require_index = require("../../../hooks/use-namespace/index.js");
const require_constants = require("./constants.js");
const require_tab_bar = require("./tab-bar.js");
let _vueuse_core = require("@vueuse/core");
let vue = require("vue");
//#region ../../packages/components/tabs/src/tab-bar.vue?vue&type=script&setup=true&lang.ts
const COMPONENT_NAME = "ElTabBar";
var tab_bar_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ (0, vue.defineComponent)({
	name: COMPONENT_NAME,
	__name: "tab-bar",
	props: require_tab_bar.tabBarProps,
	setup(__props, { expose: __expose }) {
		const props = __props;
		const rootTabs = (0, vue.inject)(require_constants.tabsRootContextKey);
		if (!rootTabs) require_error.throwError(COMPONENT_NAME, "<el-tabs><el-tab-bar /></el-tabs>");
		const ns = require_index.useNamespace("tabs");
		const barRef = (0, vue.ref)();
		const barStyle = (0, vue.ref)();
		const barReady = (0, vue.ref)(false);
		const mergedBarStyle = (0, vue.computed)(() => {
			if (barReady.value) return barStyle.value;
			return {
				...barStyle.value,
				transition: "none"
			};
		});
		/**
		* when defaultValue is not set, the bar is always shown.
		*
		* when defaultValue is set, the bar will be hidden until style is calculated
		* to avoid the bar showing in the wrong position on initial render.
		*/
		const barVisible = (0, vue.computed)(() => require_types.isUndefined(rootTabs.props.defaultValue) || Boolean(barStyle.value?.transform));
		const getBarStyle = () => {
			let offset = 0;
			let tabSize = 0;
			const sizeName = ["top", "bottom"].includes(rootTabs.props.tabPosition) ? "width" : "height";
			const sizeDir = sizeName === "width" ? "x" : "y";
			const position = sizeDir === "x" ? "left" : "top";
			props.tabs.every((tab) => {
				if (require_types.isUndefined(tab.paneName)) return false;
				const $el = props.tabRefs[tab.paneName];
				if (!$el) return false;
				if (!tab.active) return true;
				offset = $el[`offset${require_strings.capitalize(position)}`];
				tabSize = $el[`client${require_strings.capitalize(sizeName)}`];
				const tabStyles = window.getComputedStyle($el);
				if (sizeName === "width") {
					tabSize -= Number.parseFloat(tabStyles.paddingLeft) + Number.parseFloat(tabStyles.paddingRight);
					offset += Number.parseFloat(tabStyles.paddingLeft);
				}
				return false;
			});
			return {
				[sizeName]: `${tabSize}px`,
				transform: `translate${require_strings.capitalize(sizeDir)}(${offset}px)`
			};
		};
		const update = () => {
			barStyle.value = getBarStyle();
			if (!barReady.value) require_raf.rAF(() => require_raf.rAF(() => {
				barReady.value = true;
			}));
		};
		const tabObservers = [];
		const observerTabs = () => {
			tabObservers.forEach((observer) => observer.stop());
			tabObservers.length = 0;
			Object.values(props.tabRefs).forEach((tab) => {
				tabObservers.push((0, _vueuse_core.useResizeObserver)(tab, update));
			});
		};
		(0, vue.watch)(() => props.tabs, async () => {
			await (0, vue.nextTick)();
			update();
			observerTabs();
		}, { immediate: true });
		const barObserver = (0, _vueuse_core.useResizeObserver)(barRef, () => update());
		(0, vue.onBeforeUnmount)(() => {
			tabObservers.forEach((observer) => observer.stop());
			tabObservers.length = 0;
			barObserver.stop();
		});
		__expose({
			/** @description tab root html element */
			ref: barRef,
			/** @description method to manually update tab bar style, return the updated style */
			update
		});
		return (_ctx, _cache) => {
			return (0, vue.openBlock)(), (0, vue.createElementBlock)("div", {
				ref_key: "barRef",
				ref: barRef,
				class: (0, vue.normalizeClass)([
					(0, vue.unref)(ns).e("active-bar"),
					(0, vue.unref)(ns).is((0, vue.unref)(rootTabs).props.tabPosition),
					(0, vue.unref)(ns).is("hidden", !barVisible.value)
				]),
				style: (0, vue.normalizeStyle)(mergedBarStyle.value)
			}, null, 6);
		};
	}
});
//#endregion
exports.default = tab_bar_vue_vue_type_script_setup_true_lang_default;

//# sourceMappingURL=tab-bar.vue_vue_type_script_setup_true_lang.js.map