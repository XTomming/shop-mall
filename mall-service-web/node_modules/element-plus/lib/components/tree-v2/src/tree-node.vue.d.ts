import { CheckboxValueType } from "../../checkbox/src/checkbox.js";
import { TreeNode, TreeNodeProps } from "./types.js";
import * as _$vue from "vue";

//#region ../../packages/components/tree-v2/src/tree-node.vue.d.ts
declare const __VLS_export: _$vue.DefineComponent<TreeNodeProps, {}, {}, {}, {}, _$vue.ComponentOptionsMixin, _$vue.ComponentOptionsMixin, {
  check: (node: TreeNode, checked: CheckboxValueType) => void;
  click: (node: TreeNode, e: MouseEvent) => void;
  drop: (node: TreeNode, e: DragEvent) => void;
  toggle: (node: TreeNode) => void;
}, string, _$vue.PublicProps, Readonly<TreeNodeProps> & Readonly<{
  onCheck?: ((node: TreeNode, checked: CheckboxValueType) => any) | undefined;
  onClick?: ((node: TreeNode, e: MouseEvent) => any) | undefined;
  onDrop?: ((node: TreeNode, e: DragEvent) => any) | undefined;
  onToggle?: ((node: TreeNode) => any) | undefined;
}>, {
  node: TreeNode;
  itemSize: number;
}, {}, {}, {}, string, _$vue.ComponentProvideOptions, false, {}, any>;
declare const _default: typeof __VLS_export;
//#endregion
export { _default as default };