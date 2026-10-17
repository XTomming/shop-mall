import { EpPropFinalized, EpPropMergeType } from "../../../utils/vue/props/types.js";
import { Arrayable, ClassValue } from "../../../utils/typescript.js";
import { Measurable } from "../../popper/src/constants.js";
import { PopperEffect } from "../../popper/src/popper.js";
import { Options, Placement } from "../../popper/index.js";
import { TooltipTriggerType } from "../../tooltip/src/trigger.js";
import { UseTooltipProps } from "../../tooltip/src/tooltip.js";
import _default from "./popover.vue.js";
import * as _$vue from "vue";
import { ExtractPublicPropTypes } from "vue";

//#region ../../packages/components/popover/src/popover.d.ts
interface PopoverProps extends Omit<UseTooltipProps, 'ariaLabel' | 'gpuAcceleration' | 'rawContent'> {
  /**
   * @description [tabindex](https://developer.mozilla.org/en-US/docs/Web/HTML/Global_attributes/tabindex) of Popover
   */
  tabindex?: string | number;
  /**
   * @description popover title
   */
  title?: string;
  /**
   * @description popover width
   */
  width?: string | number;
}
/**
 * @deprecated Removed after 3.0.0, Use `PopoverProps` instead.
 */
declare const popoverProps: {
  readonly placement: EpPropFinalized<(new (...args: any[]) => "auto" | "left" | "right" | "bottom" | "top" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement) | (((new (...args: any[]) => "auto" | "left" | "right" | "bottom" | "top" | "auto-start" | "auto-end" | "top-start" | "top-end" | "bottom-start" | "bottom-end" | "right-start" | "right-end" | "left-start" | "left-end") | (() => Placement)) | null)[], unknown, unknown, "bottom", boolean>;
  readonly tabindex: EpPropFinalized<(new (...args: any[]) => string | number) | (() => string | number) | (((new (...args: any[]) => string | number) | (() => string | number)) | null)[], unknown, unknown, 0, boolean>;
  readonly effect: {
    readonly default: "light";
    readonly type: _$vue.PropType<EpPropMergeType<(new (...args: any[]) => string) | (() => PopperEffect) | (((new (...args: any[]) => string) | (() => PopperEffect)) | null)[], unknown, unknown>>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    readonly __epPropKey: true;
  };
  readonly title: StringConstructor;
  readonly width: EpPropFinalized<readonly [StringConstructor, NumberConstructor], unknown, unknown, 150, boolean>;
  readonly offset: {
    readonly default: undefined;
    readonly type: _$vue.PropType<number>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    readonly __epPropKey: true;
  };
  readonly persistent: EpPropFinalized<unknown, unknown, unknown, true, boolean>;
  readonly visible: EpPropFinalized<(new (...args: any[]) => boolean) | (() => boolean | null) | (((new (...args: any[]) => boolean) | (() => boolean | null)) | null)[], unknown, unknown, null, boolean>;
  readonly id: StringConstructor;
  readonly disabled: BooleanConstructor;
  readonly className: {
    readonly type: _$vue.PropType<EpPropMergeType<(new (...args: any[]) => string | false | Record<string, any> | ClassValue[]) | (() => ClassValue) | (((new (...args: any[]) => string | false | Record<string, any> | ClassValue[]) | (() => ClassValue)) | null)[], unknown, unknown>>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly style: EpPropFinalized<(new (...args: any[]) => string | false | _$vue.CSSProperties | _$vue.StyleValue[]) | (() => _$vue.StyleValue) | (((new (...args: any[]) => string | false | _$vue.CSSProperties | _$vue.StyleValue[]) | (() => _$vue.StyleValue)) | null)[], unknown, unknown, undefined, boolean>;
  readonly content: EpPropFinalized<StringConstructor, unknown, unknown, "", boolean>;
  readonly open: BooleanConstructor;
  readonly role: EpPropFinalized<StringConstructor, "listbox" | "grid" | "menu" | "tooltip" | "dialog" | "group" | "navigation" | "tree", unknown, "tooltip", boolean>;
  readonly onBlur: {
    readonly type: _$vue.PropType<(e: FocusEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly onClick: {
    readonly type: _$vue.PropType<(e: PointerEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly onContextmenu: {
    readonly type: _$vue.PropType<(e: PointerEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly onFocus: {
    readonly type: _$vue.PropType<(e: FocusEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly onKeydown: {
    readonly type: _$vue.PropType<(e: KeyboardEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly onMouseenter: {
    readonly type: _$vue.PropType<(e: MouseEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly onMouseleave: {
    readonly type: _$vue.PropType<(e: MouseEvent) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly transition: StringConstructor;
  readonly zIndex: NumberConstructor;
  readonly teleported: EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
  readonly appendTo: {
    readonly type: _$vue.PropType<EpPropMergeType<(new (...args: any[]) => string | HTMLElement) | (() => string | HTMLElement) | (((new (...args: any[]) => string | HTMLElement) | (() => string | HTMLElement)) | null)[], unknown, unknown>>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly enterable: EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
  readonly pure: BooleanConstructor;
  readonly focusOnShow: BooleanConstructor;
  readonly trapping: BooleanConstructor;
  readonly popperClass: EpPropFinalized<(new (...args: any[]) => string | false | Record<string, any> | ClassValue[]) | (() => ClassValue) | (((new (...args: any[]) => string | false | Record<string, any> | ClassValue[]) | (() => ClassValue)) | null)[], unknown, unknown, undefined, boolean>;
  readonly popperStyle: EpPropFinalized<(new (...args: any[]) => string | false | _$vue.CSSProperties | _$vue.StyleValue[]) | (() => _$vue.StyleValue) | (((new (...args: any[]) => string | false | _$vue.CSSProperties | _$vue.StyleValue[]) | (() => _$vue.StyleValue)) | null)[], unknown, unknown, undefined, boolean>;
  readonly referenceEl: {
    readonly type: _$vue.PropType<HTMLElement>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly triggerTargetEl: {
    readonly type: _$vue.PropType<HTMLElement>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly stopPopperMouseEvent: EpPropFinalized<BooleanConstructor, unknown, unknown, true, boolean>;
  readonly virtualTriggering: BooleanConstructor;
  readonly loop: BooleanConstructor;
  readonly boundariesPadding: EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
  readonly fallbackPlacements: EpPropFinalized<(new (...args: any[]) => Placement[]) | (() => Placement[]) | (((new (...args: any[]) => Placement[]) | (() => Placement[])) | null)[], unknown, unknown, undefined, boolean>;
  readonly popperOptions: EpPropFinalized<(new (...args: any[]) => Partial<Options>) | (() => Partial<Options>) | (((new (...args: any[]) => Partial<Options>) | (() => Partial<Options>)) | null)[], unknown, unknown, () => {}, boolean>;
  readonly strategy: EpPropFinalized<StringConstructor, "absolute" | "fixed", unknown, "absolute", boolean>;
  readonly arrowOffset: EpPropFinalized<NumberConstructor, unknown, unknown, 5, boolean>;
  readonly showArrow: EpPropFinalized<BooleanConstructor, unknown, unknown, boolean, boolean>;
  readonly "onUpdate:visible": {
    readonly type: _$vue.PropType<(val: boolean) => void>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
  readonly showAfter: EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
  readonly hideAfter: EpPropFinalized<NumberConstructor, unknown, unknown, 200, boolean>;
  readonly autoClose: EpPropFinalized<NumberConstructor, unknown, unknown, 0, boolean>;
  readonly trigger: EpPropFinalized<(new (...args: any[]) => "click" | "contextmenu" | "focus" | "hover" | TooltipTriggerType[]) | (() => Arrayable<TooltipTriggerType>) | (((new (...args: any[]) => "click" | "contextmenu" | "focus" | "hover" | TooltipTriggerType[]) | (() => Arrayable<TooltipTriggerType>)) | null)[], unknown, unknown, "hover", boolean>;
  readonly triggerKeys: EpPropFinalized<(new (...args: any[]) => string[]) | (() => string[]) | (((new (...args: any[]) => string[]) | (() => string[])) | null)[], unknown, unknown, () => string[], boolean>;
  readonly focusOnTarget: BooleanConstructor;
  readonly virtualRef: {
    readonly type: _$vue.PropType<Measurable>;
    readonly required: false;
    readonly validator: ((val: unknown) => boolean) | undefined;
    __epPropKey: true;
  };
};
/**
 * @deprecated Removed after 3.0.0, Use `PopoverProps` instead.
 */
type PopoverPropsPublic = ExtractPublicPropTypes<typeof popoverProps>;
declare const popoverEmits: {
  'update:visible': (value: boolean) => boolean;
  'before-enter': () => boolean;
  'before-leave': () => boolean;
  'after-enter': () => boolean;
  'after-leave': () => boolean;
};
type PopoverEmits = typeof popoverEmits;
type PopoverInstance = InstanceType<typeof _default> & unknown;
/**
 * @description default values for PopoverProps
 */
declare const popoverPropsDefaults: {
  readonly title: undefined;
  readonly tabindex: 0;
  readonly effect: "light";
  readonly width: 150;
  readonly offset: undefined;
  readonly showArrow: true;
  readonly persistent: true;
  readonly arrowOffset: 5;
  readonly trigger: "hover";
  readonly triggerKeys: () => string[];
  readonly visible: null;
  readonly style: undefined;
  readonly content: "";
  readonly teleported: true;
  readonly enterable: true;
  readonly pure: false;
  readonly focusOnShow: false;
  readonly trapping: false;
  readonly popperClass: undefined;
  readonly popperStyle: undefined;
  readonly stopPopperMouseEvent: true;
  readonly virtualTriggering: false;
  readonly loop: false;
  readonly boundariesPadding: 0;
  readonly placement: "bottom";
  readonly popperOptions: () => {};
  readonly strategy: "absolute";
  readonly showAfter: 0;
  readonly hideAfter: 200;
  readonly autoClose: 0;
};
//#endregion
export { PopoverEmits, PopoverInstance, PopoverProps, PopoverPropsPublic, popoverEmits, popoverProps, popoverPropsDefaults };