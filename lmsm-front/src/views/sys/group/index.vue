<template>
  <div class="app-container">
    <el-row :gutter="20" class="header">
      <el-col :span="7">
        <el-input placeholder="请输入小组名称..." v-model="queryForm.query" clearable
        ></el-input>
      </el-col>
      <el-button type="primary" :icon="Search" @click="initCrowdList">搜索</el-button>
      <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
      <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
        <template #reference>
          <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
        </template>
      </el-popconfirm>
    </el-row>
    <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="id" label="组号" width="100" align="center"/>
      <el-table-column prop="groupName" label="组名" width="100"
                       align="center"/>
      <el-table-column prop="memberNum" label="成员数量" width="100" align="center">
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="200"
                       align="center">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="备注"/>
      <el-table-column prop="action" label="操作" width="400" fixed="right"
                       align="center">
        <template v-slot="scope">
          <!-- 通知按钮，带消息数和图标 -->
          <template v-if="scope.row.crowdApplications?.length > 0">
            <el-badge :value="scope.row.crowdApplications.length" class="item">
              <el-button type="primary" @click="openNotification(scope.row.crowdApplications)" :icon="Bell">
                申请
              </el-button>
            </el-badge>
          </template>
          <template v-else>
            <el-button type="primary" @click="openNotification(scope.row.crowdApplications)" :icon="Bell" class="item">
              申请
            </el-button>
          </template>
          <el-button type="primary" :icon="Edit" @click="handleDialogValue(scope.row.id)" style=" margin-left: 15px"/>
          <el-popconfirm title="您确定要删除这条记录吗？" @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button type="danger" :icon="Delete"/>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <el-pagination
      v-model:current-page="queryForm.pageNum"
      v-model:page-size="queryForm.pageSize"
      :page-sizes="[10, 20, 30, 40]"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
  />
  <Dialog v-model="dialogVisible" :id="id" :dialogTitle="dialogTitle" @initCrowdList="initCrowdList"/>

  <el-dialog v-model="applyDialogVisible" title="申请详情" width="500px">
    <div v-if="applicationList.length > 0">
      <el-descriptions
          v-for="(application, index) in applicationList"
          :key="application.id"
          :title="'申请 #' + (index + 1)"
          :column="1"
          border
          class="mb-4"
      >
        <el-descriptions-item label="申请人ID">{{ application.userId }}</el-descriptions-item>
        <el-descriptions-item label="申请理由">{{ application.reason }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ formatDate(application.applyTime) }}</el-descriptions-item>
        <el-descriptions-item label="操作">
          <el-button type="success" size="small" @click="handleApprove(application)">通过</el-button>
          <el-button type="danger" size="small" @click="handleReject(application)">拒绝</el-button>
        </el-descriptions-item>
      </el-descriptions>
    </div>
    <div v-else>
      暂无申请记录
    </div>

    <template #footer>
      <el-button @click="applyDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import {Search, Delete, DocumentAdd, Edit, Tools, RefreshRight, Bell} from '@element-plus/icons-vue'
import requestUtil, {getServerUrl} from "@/util/request";
import {ref, h} from 'vue'
import Dialog from './components/dialog'
import {ElMessage, ElMessageBox, ElNotification} from 'element-plus'
import {formatDate} from '@/util/formatDate'

const tableData = ref([])

const total = ref(0)

// 处理申请
const applyDialogVisible = ref(false)
const applicationList = ref(null)

const queryForm = ref({
  query: '',
  pageNum: 1,
  pageSize: 10
})

// 弹窗需要的数据
const dialogVisible = ref(false)
const dialogTitle = ref("")
const id = ref(-1)

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
  const res = await requestUtil.post("group/delete", ids)
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
    initCrowdList();
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
  }
}

const handleDialogValue = (crowdId) => {
  if (crowdId) {
    id.value = crowdId;
    dialogTitle.value = "小组修改"
  } else {
    id.value = -1;
    dialogTitle.value = "新建小组"
  }
  dialogVisible.value = true
}

const initCrowdList = async () => {
  if (!sessionStorage.getItem('token')) {
    console.log("无token，取消请求 group/list");
    return;
  }
  const res = await requestUtil.post("group/list", queryForm.value);
  tableData.value = res.data.crowdList;
  total.value = res.data.total;
  console.log("tableData: " + tableData.value);
  console.log("total: " + total.value);

}
initCrowdList();

// 每页记录数发生变化调用
const handleSizeChange = (pageSize) => {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = pageSize;
  initCrowdList();
}

// 当前页码发生变化调用
const handleCurrentChange = (pageNum) => {
  queryForm.value.pageNum = pageNum;
  initCrowdList();
}

const openNotification = (applications) => {
  applicationList.value = applications
  applyDialogVisible.value = true
}

const handleApprove = async (application) => {
  const res = await requestUtil.post("group/apply", application)
  if (res.data.code === 200) {
    ElMessage.success("申请已通过")
    initCrowdList()
  } else {
    ElMessage.error(res.data.msg)
  }
}

const handleReject = async (id) => {
  const res = await requestUtil.post("group/reject", {id: id})
  if (res.data.code === 200) {
    ElMessage.success("申请已拒绝")
    initCrowdList()
  } else {
    ElMessage.error(res.data.msg)
  }
}
</script>

<style scoped lang="scss">
.header {
  padding-bottom: 16px;
  box-sizing: border-box;
}

.el-pagination {
  float: right;
  padding: 20px;
  box-sizing: border-box;
}

::v-deep th.el-table__cell {
  word-break: break-word;
  background-color: #f8f8f9 !important;
  color: #515a6e;
  height: 40px;
  font-size: 13px;
}

.el-tag--small {
  margin-left: 5px;
}

.item {
  margin: 20px;
}
</style>