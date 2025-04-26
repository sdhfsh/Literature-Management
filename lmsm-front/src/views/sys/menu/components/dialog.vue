<template>
  <el-dialog
      :model-value="props.modelValue"
      :title="dialogTitle"
      width="30%"
      @close="handleClose"
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
    >
      <el-form-item label="上级菜单" prop="parentId">
        <el-select v-model="form.parentId" placeholder="请选择上级菜单">
          <template v-for="item in tableData" :key="item.id">
            <el-option :label="item.name" :value="item.id"/>
            <template v-if="item.children && item.children.length">
              <el-option
                  v-for="child in item.children"
                  :key="child.id"
                  :label="'-- ' + child.name"
                  :value="child.id"
              />
            </template>
          </template>
        </el-select>
      </el-form-item>

      <el-form-item label="菜单类型" prop="menuType">
        <el-radio-group v-model="form.menuType">
          <el-radio label="M">目录</el-radio>
          <el-radio label="C">菜单</el-radio>
          <el-radio label="F">按钮</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="菜单图标" prop="icon">
        <el-input v-model="form.icon"/>
      </el-form-item>

      <el-form-item label="菜单名称" prop="name">
        <el-input v-model="form.name"/>
      </el-form-item>

      <el-form-item label="权限标识" prop="perms">
        <el-input v-model="form.perms"/>
      </el-form-item>

      <el-form-item label="组件路径" prop="component">
        <el-input v-model="form.component"/>
      </el-form-item>

      <el-form-item label="显示顺序" prop="orderNum">
        <el-input-number v-model="form.orderNum" :min="1"/>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">确认</el-button>
        <el-button @click="handleClose">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {defineEmits, defineProps, ref, watch} from "vue";
import requestUtil from "@/util/request";
import {ElMessage} from 'element-plus';

const formRef = ref(null);
const tableData = ref([]);

const props = defineProps({
  modelValue: {
    type: Boolean,
    required: true
  },
  id: {
    type: Number,
    default: -1,
    required: true
  },
  dialogTitle: {
    type: String,
    default: '',
    required: true
  },
  tableData: {
    type: Array,
    default: () => [],
    required: true
  }
});


const emits = defineEmits(['update:modelValue', 'initMenuList']);

const form = ref({
  id: -1,
  parentId: '',
  menuType: "M",
  icon: '',
  name: '',
  perms: '',
  component: '',
  orderNum: 1
});

const rules = {
  parentId: [{required: true, message: '请选择上级菜单'}],
  name: [{required: true, message: '菜单名称不能为空', trigger: 'blur'}]
};

const initFormData = async (id) => {
  const res = await requestUtil.get("menu/" + id);
  form.value = res.data.menu;
};

watch(() => props.modelValue, (visible) => {
  console.log("弹窗打开了吗？", visible);
  if (visible) {
    console.log("收到的 tableData：", props.tableData);
    // 等待主页面加载完菜单数据再赋值
    tableData.value = Array.isArray(props.tableData) ? props.tableData : [];

    if (props.id !== -1) {
      initFormData(props.id);
    } else {
      form.value = {
        id: -1,
        parentId: '',
        menuType: "M",
        icon: '',
        name: '',
        perms: '',
        component: '',
        orderNum: 1
      };
    }
  }
});

const handleClose = () => {
  emits('update:modelValue', false);
};

const handleConfirm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const res = await requestUtil.post("menu/save", form.value);
      const data = res.data;
      if (data.code === 200) {
        ElMessage.success("执行成功！");
        formRef.value.resetFields();
        emits("initMenuList");
        handleClose();
      } else {
        ElMessage.error(data.msg);
      }
    }
  });
};
</script>
