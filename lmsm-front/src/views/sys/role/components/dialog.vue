<template>
  <el-dialog
      v-model="visible"
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
      <el-form-item label="角色名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>

      <el-form-item label="权限字符" prop="code">
        <el-input v-model="form.code" />
      </el-form-item>

      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="4" />
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
import { ref, watch, defineProps, defineEmits } from "vue"
import requestUtil from "@/util/request"
import { ElMessage } from 'element-plus'

// 接收来自父组件的 prop
const props = defineProps({
  modelValue: Boolean,
  id: Number,
  dialogTitle: String
})

// 响应式绑定 dialog 的显示状态
const visible = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
})

const emit = defineEmits(['update:modelValue', 'initRoleList'])

const handleClose = () => {
  emit('update:modelValue', false)
}

// 表单数据
const form = ref({
  id: -1,
  name: "",
  code: "",
  remark: ""
})

const rules = ref({
  name: [
    { required: true, message: '请输入角色名称' },
  ],
  code: [
    { required: true, message: '请输入权限字符' },
  ],
})

const formRef = ref(null)

const initFormData = async (id) => {
  const res = await requestUtil.get("role/" + id)
  form.value = res.data.role
}

// 弹窗打开时加载数据
watch(() => visible.value, async (newVal) => {
  if (newVal) {
    if (props.id !== -1) {
      await initFormData(props.id)
    } else {
      form.value = {
        id: -1,
        name: "",
        code: "",
        remark: ""
      }
    }
  }
})

const handleConfirm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      const result = await requestUtil.post("role/save", form.value)
      const data = result.data
      if (data.code === 200) {
        ElMessage.success("执行成功！")
        formRef.value.resetFields()
        emit("initRoleList")
        handleClose()
      } else {
        ElMessage.error(data.msg)
      }
    }
  })
}
</script>

<style scoped>
</style>
