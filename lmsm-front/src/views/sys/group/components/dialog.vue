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
      <el-form-item label="组名" prop="groupName">
        <el-input v-model="form.groupName"/>
      </el-form-item>

      <el-form-item label="备注" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="4" />
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

const emit = defineEmits(['update:modelValue', 'initCrowdList'])

const handleClose = () => {
  emit('update:modelValue', false)
}

// 表单数据
const form = ref({
  id: -1,
  groupName: "",
  description: ""
})

// 校验规则
const checkUsername = async (rule, value, callback) => {
  if (form.value.id === -1) {
    const res = await requestUtil.post("user/checkUserName", { username: value })
    if (res.data.code === 500) {
      callback(new Error("用户名已存在！"))
    } else {
      callback()
    }
  } else {
    callback()
  }
}

const rules = ref({
  username: [
    { required: true, message: '请输入用户名' },
    { validator: checkUsername, trigger: "blur" }
  ],
  email: [
    { required: true, message: "邮箱地址不能为空", trigger: "blur" },
    { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
  ],
  phone: [
    { required: true, message: "手机号码不能为空", trigger: "blur" },
    { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
  ]
})

const formRef = ref(null)

const initFormData = async (id) => {
  const res = await requestUtil.get("group/" + id)
  form.value = res.data.crowd
}

// 弹窗打开时加载数据
watch(() => visible.value, async (newVal) => {
  if (newVal) {
    if (props.id !== -1) {
      await initFormData(props.id)
    } else {
      form.value = {
        id: -1,
        groupName: "",
        description: ""
      }
    }
  }
})

const handleConfirm = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      console.log("form", form.value)
      const result = await requestUtil.post("group/save", form.value)
      const data = result.data
      if (data.code === 200) {
        ElMessage.success("执行成功！")
        formRef.value.resetFields()
        emit("initCrowdList")
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
