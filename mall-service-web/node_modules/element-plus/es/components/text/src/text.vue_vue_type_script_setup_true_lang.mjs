import { isUndefined } from "../../../utils/types.mjs";
import { cAF, rAF } from "../../../utils/raf.mjs";
import { useNamespace } from "../../../hooks/use-namespace/index.mjs";
import { useFormSize } from "../../form/src/hooks/use-form-common-props.mjs";
import { textProps } from "./text.mjs";
import { useElementSize, useMutationObserver } from "@vueuse/core";
import { computed, createBlock, defineComponent, normalizeClass, normalizeStyle, onBeforeUnmount, onMounted, openBlock, ref, renderSlot, resolveDynamicComponent, watch, withCtx } from "vue";
//#region ../../packages/components/text/src/text.vue?vue&type=script&setup=true&lang.ts
var text_vue_vue_type_script_setup_true_lang_default = /*@__PURE__*/ defineComponent({
	name: "ElText",
	__name: "text",
	props: textProps,
	setup(__props, { expose: __expose }) {
		const props = __props;
		const textRef = ref();
		const textSize = useFormSize();
		const ns = useNamespace("text");
		const textKls = computed(() => [
			ns.b(),
			ns.m(props.type),
			ns.m(textSize.value),
			ns.is("truncated", props.truncated),
			ns.is("line-clamp", !isUndefined(props.lineClamp))
		]);
		const { width, height } = useElementSize(textRef);
		const isTruncated = ref(false);
		let rafId;
		const bindTitle = () => {
			if (rafId) cAF(rafId);
			rafId = rAF(() => {
				rafId = void 0;
				isTruncated.value = false;
				if (props.truncated) {
					const width = textRef.value?.offsetWidth;
					const scrollWidth = textRef.value?.scrollWidth;
					if (width && scrollWidth && scrollWidth > width) isTruncated.value = true;
				} else if (!isUndefined(props.lineClamp)) {
					const height = textRef.value?.offsetHeight;
					const scrollHeight = textRef.value?.scrollHeight;
					if (height && scrollHeight && scrollHeight > height) isTruncated.value = true;
				}
			});
		};
		watch(() => [
			width.value,
			height.value,
			props.truncated,
			props.lineClamp
		], bindTitle, { flush: "post" });
		useMutationObserver(textRef, bindTitle, {
			attributes: true,
			attributeFilter: ["class", "style"],
			subtree: true,
			childList: true,
			characterData: true
		});
		onMounted(bindTitle);
		onBeforeUnmount(() => {
			if (rafId) cAF(rafId);
		});
		__expose({ 
		/** @description whether the text is truncated */
isTruncated });
		return (_ctx, _cache) => {
			return openBlock(), createBlock(resolveDynamicComponent(__props.tag), {
				ref_key: "textRef",
				ref: textRef,
				class: normalizeClass(textKls.value),
				title: _ctx.$attrs.title ?? (isTruncated.value ? textRef.value?.textContent : void 0),
				style: normalizeStyle({ "-webkit-line-clamp": __props.lineClamp })
			}, {
				default: withCtx(() => [renderSlot(_ctx.$slots, "default")]),
				_: 3
			}, 8, [
				"class",
				"title",
				"style"
			]);
		};
	}
});
//#endregion
export { text_vue_vue_type_script_setup_true_lang_default as default };

//# sourceMappingURL=text.vue_vue_type_script_setup_true_lang.mjs.map