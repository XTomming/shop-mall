import { InputNumberProps } from "./input-number.js";
import * as _$vue from "vue";

//#region ../../packages/components/input-number/src/input-number.vue.d.ts
declare var __VLS_1: {}, __VLS_19: {}, __VLS_52: {}, __VLS_55: {};
type __VLS_Slots = {} & {
  'decrease-icon'?: (props: typeof __VLS_1) => any;
} & {
  'increase-icon'?: (props: typeof __VLS_19) => any;
} & {
  prefix?: (props: typeof __VLS_52) => any;
} & {
  suffix?: (props: typeof __VLS_55) => any;
};
declare const __VLS_base: _$vue.DefineComponent<InputNumberProps, {
  /** @description get focus the input component */focus: () => void; /** @description remove focus the input component */
  blur: () => void;
}, {}, {}, {}, _$vue.ComponentOptionsMixin, _$vue.ComponentOptionsMixin, {
  "update:modelValue": (val: number | undefined) => void;
  change: (cur: number | undefined, prev: number | undefined) => void;
  blur: (e: FocusEvent) => void;
  focus: (e: FocusEvent) => void;
  input: (val: number | null | undefined) => void;
}, string, _$vue.PublicProps, Readonly<InputNumberProps> & Readonly<{
  "onUpdate:modelValue"?: ((val: number | undefined) => any) | undefined;
  onChange?: ((cur: number | undefined, prev: number | undefined) => any) | undefined;
  onInput?: ((val: number | null | undefined) => any) | undefined;
  onBlur?: ((e: FocusEvent) => any) | undefined;
  onFocus?: ((e: FocusEvent) => any) | undefined;
}>, {
  id: string;
  disabled: boolean;
  min: number;
  max: number;
  validateEvent: boolean;
  valueOnClear: "min" | "max" | number | null;
  readonly: boolean;
  tabindex: string | number;
  inputmode: "none" | "text" | "search" | "email" | "tel" | "url" | "numeric" | "decimal";
  align: "left" | "right" | "center";
  step: number;
  stepStrictly: boolean;
  controls: boolean;
  controlsPosition: "" | "right";
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