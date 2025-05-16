<template>
  <el-dialog
      v-model="visible"
      :title="dialogTitle"
      width="50%"
      @close="handleClose"
  >
    <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
    >
      <el-form-item label="标题" prop="title">
        <el-input v-model="form.title"/>
      </el-form-item>

      <el-form-item label="作者" prop="author">
        <el-input v-model="form.author"/>
      </el-form-item>

      <el-form-item label="摘要" prop="excerpt">
        <el-input v-model="form.excerpt" type="textarea" :rows="2"/>
      </el-form-item>

      <el-form-item label="期刊名称" prop="journal">
        <el-input v-model="form.journal"/>
      </el-form-item>

      <el-form-item label="分类" prop="category">
        <el-input-tag
            v-model="form.category"
            placeholder="请输入或选择文献分类标签（唯一）"
            clearable
            draggable
        >
          <!-- 下拉框 -->
          <template #prefix>
            <el-select v-model="selectedTag" placeholder="选择标签" @change="handleTagSelect">
              <el-option
                  v-for="tag in systemTags"
                  :key="tag"
                  :label="tag"
                  :value="tag"
              />
            </el-select>
          </template>
        </el-input-tag>
      </el-form-item>
      <el-form-item label="发表日期" prop="publication_data">
        <el-date-picker v-model="form.publication_data" type="date" value-format="YYYY-MM-DD"
                        placeholder="请选择日期"></el-date-picker>
      </el-form-item>
      <el-form-item label="出版机构" prop="publisher">
        <el-input v-model="form.publisher"/>
      </el-form-item>
      <el-form-item label="卷号" prop="volume">
        <el-input v-model="form.volume"/>
      </el-form-item>
      <el-form-item label="期号" prop="issue">
        <el-input v-model="form.issue"/>
      </el-form-item>
      <el-form-item label="页码范围" prop="pages">
        <el-input v-model="form.pages"/>
      </el-form-item>
      <el-form-item label="doi" prop="doi">
        <el-input v-model="form.doi"/>
      </el-form-item>
      <el-form-item label="url" prop="url">
        <el-input v-model="form.url"/>
      </el-form-item>
      <el-form-item label="文献类型" prop="type">
        <el-input v-model="form.type"/>
      </el-form-item>
      <el-form-item label="是否共享" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio label="private">私有</el-radio>
          <el-radio label="public">公开</el-radio>
          <el-radio label="group">小组共享</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2"/>
      </el-form-item>
      <el-form-item label="上传文献" prop="remark">
        <el-upload
            :headers="headers"
            ref="uploadRef"
            :action="getServerUrl()+'file/upload'"
            :auto-upload="true"
            :show-file-list="true"
            :on-success="handleFileUploadSuccess"
        >
          <div style="display: flex; align-items: center; justify-content: center; width: 100%;">
            <el-icon
                style="font-size: 48px; color: #409EFF; cursor: pointer;"
            >
              <UploadFilled/>
            </el-icon>
          </div>
        </el-upload>
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
import {ref, watch, defineProps, defineEmits} from "vue"
import requestUtil, {getServerUrl} from "@/util/request"
import {ElMessage} from 'element-plus'
import store from "@/store";

// 接收来自父组件的 prop
const props = defineProps({
  modelValue: Boolean,
  id: Number,
  dialogTitle: String
})

const headers = ref({
  token: store.getters.GET_TOKEN
})


// 表单数据
const form = ref({
  id: -1,
  title: "",
  author: "",
  excerpt: "",
  journal: "",
  category: [],
  publication_data: "",
  publisher: "",
  volume: "",
  issue: "",
  pages: "",
  doi: "",
  url: "",
  type: "",
  status: "private",
  file_path: "",
  file_type: ""
})

// 分类标签
const selectedTag = ref()
const systemTags = ref([
])
// 初始化系统预制标签
const initSystemTags = async () => {
  const res = await requestUtil.get("file/tags")
  systemTags.value = res.data.tags
}
initSystemTags();
const handleTagSelect = () => {
  form.value.category.push(selectedTag.value)
}


// 响应式绑定 dialog 的显示状态
const visible = ref(props.modelValue)

watch(() => props.modelValue, (newVal) => {
  visible.value = newVal
})

const emit = defineEmits(['update:modelValue', 'initDocList'])

const handleClose = () => {
  emit('update:modelValue', false)
  resetForm()
}


// 校验规则
const checkUsername = async (rule, value, callback) => {
  if (form.value.id === -1) {
    const res = await requestUtil.post("user/checkUserName", {username: value})
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
    {required: true, message: '请输入用户名'},
    {validator: checkUsername, trigger: "blur"}
  ],
  email: [
    {required: true, message: "邮箱地址不能为空", trigger: "blur"},
    {type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"]}
  ],
  phone: [
    {required: true, message: "手机号码不能为空", trigger: "blur"},
    {pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur"}
  ]
})

const formRef = ref(null)

const initFormData = async (id) => {
  const res = await requestUtil.get("file/" + id)
  form.value = res.data.document
}

// 弹窗打开时加载数据
watch(() => visible.value, async (newVal) => {
  if (newVal) {
    if (props.id !== -1) {
      await initFormData(props.id)
    } else {

    }
  }
})

const handleConfirm = async () => {
  let result = await requestUtil.post("file/sava", form.value);
  let data = result.data;
  if (data.code == 200) {
    ElMessage.success(data.msg)
    formRef.value.resetFields()
    emit("initDocList")
    handleClose();
  } else {
    ElMessage.error(data.msg);
  }
}

const handleFileUploadSuccess = (res) => {
  form.value.file_path = res.data.file_path;
  form.value.file_type = res.data.file_type;
  console.log("file_path: " + form.value.file_path);
  console.log("file_type: " + form.value.file_type);
}

const resetForm = () => {
  form.value = {
    id: -1,
    title: "",
    author: "",
    excerpt: "",
    journal: "",
    category: [],
    publication_data: "",
    publisher: "",
    volume: "",
    issue: "",
    pages: "",
    doi: "",
    url: "",
    type: "",
    status: "private",
    file_path: "",
    file_type: ""
  };
};

</script>

<style scoped>
</style>
