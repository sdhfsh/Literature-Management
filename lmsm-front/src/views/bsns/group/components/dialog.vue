<template>
  <el-dialog
      v-model="visible"
      title="小组详情"
      width="30%"
      @close="handleClose"
  >
    <el-card style="width: 425px" shadow="always">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="小组名称">{{ props.crowd.groupName }}</el-descriptions-item>
        <el-descriptions-item label="简介">{{ props.crowd.description }}</el-descriptions-item>
        <el-descriptions-item label="创建者">{{props.crowd.createdBy}}</el-descriptions-item>
      </el-descriptions>
    </el-card>
    <template #footer>
      <span class="dialog-footer">
        <el-button type="primary" @click="handleConfirm">申请加入</el-button>
        <el-button @click="handleClose">取消</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, watch, defineProps, defineEmits} from "vue"
import requestUtil from "@/util/request"
import {ElMessage} from 'element-plus'

// 接收来自父组件的 prop
const props = defineProps({
  modelValue: Boolean,
  crowd: Object
})

// 响应式绑定 dialog 的显示状态
const visible = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
})

const emit = defineEmits(['update:modelValue'])

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleConfirm = async () => {
  const res = await requestUtil.post("group/join", props.crowd.id)
  if (res.data.code === 200) {
    ElMessage.success(res.data.msg)
    emit('update:modelValue', false)
  } else {
    ElMessage.success("加入失败")
  }
}
</script>

<style scoped>
</style>
