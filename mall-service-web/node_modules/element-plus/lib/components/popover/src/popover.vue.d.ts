import { Arrayable, ClassValue } from "../../../utils/typescript.js";
import { PopperEffect, PopperInstance } from "../../popper/src/popper.js";
import { Options, Placement } from "../../popper/index.js";
import { TooltipTriggerType } from "../../tooltip/src/trigger.js";
import { PopoverProps } from "./popover.js";
import * as _$vue from "vue";

//#region ../../packages/components/popover/src/popover.vue.d.ts
declare var __VLS_14: {}, __VLS_17: {
    hide: () => void;
  };
type __VLS_Slots = {} & {
  reference?: (props: typeof __VLS_14) => any;
} & {
  default?: (props: typeof __VLS_17) => any;
};
declare const __VLS_base: _$vue.DefineComponent<PopoverProps, {
  /** @description popper ref */popperRef: _$vue.ComputedRef<PopperInstance | undefined>; /** @description hide popover */
  hide: () => void;
}, {}, {}, {}, _$vue.ComponentOptionsMixin, _$vue.ComponentOptionsMixin, {
  "update:visible": (value: boolean) => void;
  "before-enter": () => void;
  "before-leave": () => void;
  "after-enter": () => void;
  "after-leave": () => void;
}, string, _$vue.PublicProps, Readonly<PopoverProps> & Readonly<{
  "onUpdate:visible"?: ((value: boolean) => any) | undefined;
  "onBefore-enter"?: (() => any) | undefined;
  "onBefore-leave"?: (() => any) | undefined;
  "onAfter-enter"?: (() => any) | undefined;
  "onAfter-leave"?: (() => any) | undefined;
}>, {
  visible: boolean | null;
  style: string | false | _$vue.CSSProperties | _$vue.StyleValue[] | null;
  width: string | number;
  content: string;
  title: string;
  offset: number;
  teleported: boolean;
  effect: PopperEffect;
  tabindex: string | number;
  enterable: boolean;
  pure: boolean;
  focusOnShow: boolean;
  trapping: boolean;
  popperClass: string | false | Record<string, any> | ClassValue[] | null;
  popperStyle: string | false | _$vue.CSSProperties | _$vue.StyleValue[] | null;
  stopPopperMouseEvent: boolean;
  virtualTriggering: boolean;
  loop: boolean;
  boundariesPadding: number;
  placement: Placement;
  popperOptions: Partial<Options>;
  strategy: "absolute" | "fixed";
  arrowOffset: number;
  showArrow: boolean;
  persistent: boolean;
  showAfter: number;
  hideAfter: number;
  autoClose: number;
  trigger: Arrayable<TooltipTriggerType>;
  triggerKeys: string[];
}, {}, {}, {}, string, _$vue.ComponentProvideOptions, false, {}, any>;
declare const __VLS_export: __VLS_WithSlots<typeof __VLS_base, __VLS_Slots>;
declare const _default: typeof __VLS_export;
type __VLS_WithSlots<T, S> = T & {
  new (): {
    $slots: S;
  };
};
//#endregion
export { _default as default };