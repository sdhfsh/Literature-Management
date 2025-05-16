<template>
  <el-row :gutter="20" class="header">
    <el-col :span="4">
      <el-input placeholder="请输入标题..." v-model="queryForm.query" clearable
      ></el-input>
    </el-col>
<!--    <el-col :span="4">-->
<!--      <el-input placeholder="请输入作者..." v-model="queryForm.query" clearable-->
<!--      ></el-input>-->
<!--    </el-col>-->
    <el-button type="primary" :icon="Search" @click="initDocList">搜索</el-button>
    <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
    <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
      <template #reference>
        <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
      </template>
    </el-popconfirm>
  </el-row>
  <el-table stripe ref="tableRef" row-key="id" :data="tableData" style="width: 100%"
            :header-cell-style="{ textAlign: 'center' }" :cell-style="{ textAlign: 'center' }"
            @selection-change="handleSelectionChange">
    <el-table-column type="selection" width="55"/>
    <el-table-column prop="title" label="标题" width="200"/>
    <el-table-column prop="author" label="作者" width="200"/>
    <el-table-column prop="excerpt" label="摘要" width="200">

      <template #default="scope">
        <el-tooltip
            class="custom-tooltip"
            content=" "
            placement="top-start"
        >
          <template #content>
            <div style="max-width: 300px; white-space: normal; word-break: break-word;">
              {{ scope.row.excerpt }}
            </div>
          </template>
          <div style="overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
            {{ scope.row.excerpt }}
          </div>
        </el-tooltip>
      </template>
    </el-table-column>
    <el-table-column prop="journal" label="期刊名称" width="200"/>
    <el-table-column prop="tag" label="分类" width="100" filter-placement="bottom-end">
      <template #default="scope">
        <div>
          <el-tag
              v-for="(item, index) in scope.row.category"
              :key="index"
              :type="item === '科技' ? 'primary' : 'success'"
              disable-transitions
              class="mr-1"
              style="margin-bottom: 6px;"
          >
            {{ item }}
          </el-tag>
        </div>
      </template>
    </el-table-column>
    <el-table-column prop="publication_data" label="发表日期" sortable width="200"/>
    <el-table-column prop="publisher" label="出版机构" width="200"/>
    <el-table-column prop="volume" label="卷号" width="200"/>
    <el-table-column prop="issue" label="期号" width="200"/>
    <el-table-column prop="pages" label="页码范围" width="200"/>
    <el-table-column prop="doi" label="doi" width="200"/>
    <el-table-column prop="url" label="url" width="200"/>
    <el-table-column prop="type" label="文献类型" width="200"/>
    <el-table-column prop="status" label="是否共享" width="300">
      <template #default="scope">
        <el-radio-group v-model="scope.row.status" @change="handleShareChange(scope.row)">
          <el-radio label="private">私有</el-radio>
          <el-radio label="public">公开</el-radio>
          <el-radio label="group">小组共享</el-radio>
        </el-radio-group>
      </template>
    </el-table-column>
    <el-table-column prop="date" label="创建时间" sortable width="200" column-key="date">
      <template #default="scope">
        {{ formatDate(scope.row.createdAt) }}
      </template>
    </el-table-column>
    <el-table-column prop="updatedAt" label="更新时间" sortable width="200">
      <template #default="scope">
        {{ formatDate(scope.row.updatedAt) }}
      </template>
    </el-table-column>
    <el-table-column prop="user" label="上传者" width="200">
      <template #default="scope">
        {{ scope.row.user.username }}
      </template>
    </el-table-column>
    <el-table-column fixed="right" prop="name" label="操作" width="200">
      <template v-slot="scope">
        <!--        <el-button type="primary" :icon="Tools">-->
        <!--          分配角色-->
        <!--        </el-button>-->
        <el-icon style="font-size: 28px; color: #409EFF; margin-right: 15px; cursor: pointer;"
                 @click="handleDownload(scope.row)">
          <Download/>
        </el-icon>
        <el-popconfirm title="确定要删除该文献吗？" @confirm="handleDelete(scope.row.id)">
          <template #reference>
            <el-icon
                style="font-size: 28px; color: #409EFF; margin-right: 15px; cursor: pointer;"
            >
              <DeleteFilled/>
            </el-icon>
          </template>
        </el-popconfirm>
        <el-icon style="font-size: 28px; color: #409EFF; margin-right: 15px; cursor: pointer;"
                 @click="handleDialogValue(scope.row.id)">
          <Edit/>
        </el-icon>
        <el-icon style="font-size: 28px; color: #409EFF; cursor: pointer;" @click="handlePreview(scope.row)">
          <Tickets/>
        </el-icon>
        <el-dropdown @command="(style) => generateCitation(scope.row.id, style)">
          <el-button type="primary">
            生成引用
            <el-icon class="el-icon--right">
              <arrow-down/>
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="APA">APA</el-dropdown-item>
              <el-dropdown-item command="MLA">MLA</el-dropdown-item>
              <el-dropdown-item command="GBT">GBT</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </template>
    </el-table-column>
  </el-table>
  <el-pagination
      v-model:current-page="queryForm.pageNum"
      v-model:page-size="queryForm.pageSize"
      :page-sizes="[10, 20, 30, 40]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
  />

  <Dialog v-model="dialogVisible" :id="id" :dialogTitle="dialogTitle" @initDocList="initDocList"/>
  <PdfPreview v-model="pdfDialogVisible" :filePath="previewFilePath"/>
</template>

<script setup>
import {ref, watch} from 'vue'
import {Delete, DocumentAdd, Search, Tools, ArrowDown} from "@element-plus/icons-vue";
import requestUtil from "@/util/request";
import {ElMessage, ElMessageBox} from "element-plus";
import store from "@/store";
import Dialog from './components/dialog'
import PdfPreview from "@/components/PdfPreview.vue"
import axios from "axios";
import {formatDate} from "@/util/formatDate";

// pdf预览弹窗需要
const pdfDialogVisible = ref(false)
const previewFilePath = ref("")
const handlePreview = (row) => {
  if (!row.file_path) {
    ElMessage.warning("没有文件路径，无法预览")
    return
  }
  previewFilePath.value = row.file_path
  pdfDialogVisible.value = true
}


const tableRef = ref()
const total = ref(0)
const queryForm = ref({
  query: '',
  pageNum: 1,
  pageSize: 10
})

const tableData = ref([])

// 批量删除
const delBtnStatus = ref(true)
// 定义 选中的行
const multipleSelection = ref([])

const handleSelectionChange = (selection) => {
  console.log("勾选了")
  console.log(selection)
  multipleSelection.value = selection;
  delBtnStatus.value = selection.length == 0;
}

const initDocList = async () => {
  if (!sessionStorage.getItem('token')) {
    console.log("无token，取消请求 file/init");
    return;
  }
  const res = await requestUtil.post("file/list", queryForm.value);
  tableData.value = res.data.docList;
  total.value = res.data.total;
  console.log("tableData: " + tableData.value);
  console.log("total: " + total.value);

}
initDocList();

// 弹窗需要的数据
const dialogVisible = ref(false)
const dialogTitle = ref("")
const id = ref(-1)

const handleDialogValue = (docId) => {
  console.log("docId", docId)
  if (docId) {
    id.value = docId;
    dialogTitle.value = "文献修改"
  } else {
    id.value = -1;
    dialogTitle.value = "新增文献"
  }
  dialogVisible.value = true
}

const handleDelete = async (id) => {
  var ids = []
  if (id) {
    // 单行值选中
    ids.push(id)
  } else {
    // 多行值选中
    multipleSelection.value.forEach(row => {
      ids.push(row.id)
    })
  }
  const res = await requestUtil.post("file/delete", ids)
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
    initDocList();
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
  }
}

// 每页记录数发生变化调用
const handleSizeChange = (pageSize) => {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = pageSize;
  initDocList();
}

// 当前页码发生变化调用
const handleCurrentChange = (pageNum) => {
  queryForm.value.pageNum = pageNum;
  initDocList();
}

// 下载事件
const handleDownload = async (row) => {
  const file_path = row.file_path;
  const file_type = row.file_type;

  if (!file_path) {
    ElMessage.warning("该记录没有文件路径");
    return;
  }

  try {
    await ElMessageBox.confirm(
        '是否下载该文献？',
        '提示',
        {
          confirmButtonText: '下载',
          cancelButtonText: '取消',
          type: 'info',
        }
    );

    // 使用原生 axios 发起下载请求
    const response = await axios.get('http://localhost:9999/file/download', {
      params: {filePath: file_path}, // GET 参数
      responseType: 'blob',            // 接收 Blob 数据
      headers: {
        token: store.getters.GET_TOKEN,  // 传递 token，如果需要
      }
    });

    // 构造 Blob 对象
    const blob = new Blob([response.data], {type: response.data.type || 'application/octet-stream'});

    // 提取文件名
    let fileName = file_path;
    const lastSlash = file_path.lastIndexOf('/');
    if (lastSlash !== -1) {
      fileName = file_path.substring(lastSlash + 1);
    }
    const originalFileName = fileName.substring(fileName.indexOf('-') + 1);

    // 创建下载链接并触发点击
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = originalFileName || `${row.title}.${file_type || 'pdf'}`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(link.href); // 清除 blob URL

  } catch (error) {
    console.error('文件下载失败：', error);
    ElMessage.error("下载失败，请稍后再试");
  }
};

const generateCitation = async (docId, style) => {
  const res = await requestUtil.get("file/reference", {docId, style})
  if (res.data.code === 200) {
    await navigator.clipboard.writeText(res.data.quote)
    ElMessage.success('引用已复制到剪贴板')
  } else {
    ElMessage.error(res.msg || '生成失败')
  }
}

// 更改文献共享状态
const handleShareChange = async (row) => {
  const res = await requestUtil.post("file/updateStatus", row)
  if (res.data.code === 200) {
    initDocList()
    ElMessage.success('更改成功')
  } else {
    ElMessage.error(res.msg || '生成失败')
  }
}
</script>

<style scoped>
/* 使用 ::v-deep 深度作用样式 */
::v-deep(.custom-tooltip .el-tooltip__popper) {
  background-color: #ffffff; /* 白底 */
  color: #000000; /* 黑字 */
  font-size: 14px;
  padding: 10px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  max-width: 300px;
  white-space: normal;
  word-break: break-word;
}

.el-pagination {
  float: right;
  padding: 20px;
  box-sizing: border-box;
}
</style>
