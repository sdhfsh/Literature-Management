<template>
  <el-dialog v-model="visible" width="80%" title="PDF 预览" @close="onClose">
    <iframe
        v-if="iframeSrc"
        :src="iframeSrc"
        width="100%"
        height="600px"
        style="border: none;"
    ></iframe>
  </el-dialog>
</template>

<script setup>
import {ref, watch} from 'vue'
import axios from 'axios'
import store from '@/store'
import {ElMessage} from "element-plus";

const props = defineProps({
  filePath: String,
  modelValue: Boolean
})
const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const iframeSrc = ref('')

watch(() => props.modelValue, async (val) => {
  visible.value = val
  if (val) {
    await fetchPdfBlob()
  }
})

const fetchPdfBlob = async () => {
  try {
    const response = await axios.get('http://localhost:9999/file/preview', {
      params: {filePath: props.filePath},
      responseType: 'blob',
      headers: {
        token: store.getters.GET_TOKEN
      },
      withCredentials: true
    })

    // 判断响应是否是 application/json（错误信息）
    const contentType = response.headers['content-type']
    if (contentType && contentType.includes('application/json')) {
      // 把 blob 转为 json 解析错误提示
      const text = await response.data.text()
      const error = JSON.parse(text)
      ElMessage.error(error.msg || '预览失败')
      return
    }

    // 否则是正常 PDF blob
    const blobUrl = URL.createObjectURL(response.data)
    iframeSrc.value = blobUrl
  } catch (e) {
    console.error('加载 PDF 失败', e)
    ElMessage.error('仅支持 PDF 预览')
  }
}


const onClose = () => {
  iframeSrc.value = ''
  emit('update:modelValue', false)
}
</script>
