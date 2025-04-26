<template>
  <div class="app-container">
    <el-row :gutter="20" class="header">
      <el-col :span="7">
        <el-input placeholder="请输入角色名..." v-model="queryForm.query" clearable
        ></el-input>
      </el-col>
      <el-button type="primary" :icon="Search" @click="initRoleList">搜索</el-button>
      <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
      <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
        <template #reference>
          <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
        </template>
      </el-popconfirm>
    </el-row>
    <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="name" label="角色名" width="100"
                       align="center"/>
      <el-table-column prop="code" label="权限字符" width="200" align="center"/>
      <el-table-column prop="createAt" label="创建时间" width="200"
                       align="center"/>
      <el-table-column prop="remark" label="备注"/>
      <el-table-column prop="action" label="操作" width="400" fixed="right"
                       align="center">
        <template v-slot="scope">
          <el-button type="primary" :icon="Tools" @click="handleMenuDialogValue(scope.row.id)">
            分配权限
          </el-button>
          <el-button v-if="scope.row.code!=='admin'" type="primary" :icon="Edit"
                     @click="handleDialogValue(scope.row.id)"/>
          <el-popconfirm v-if="scope.row.code!='admin'" title="您确定要删除这条记录吗？"
                         @confirm="handleDelete(scope.row.id)">
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
  <Dialog v-model="dialogVisible" :id="id" :dialogTitle="dialogTitle" @initRoleList="initRoleList"/>
  <MenuDialog v-model="menuDialogVisible" :sysRoleList="sysRoleList" :id="id" :menuDialogVisible="menuDialogVisible"
              @initRoleList="initRoleList">
  </MenuDialog>
</template>

<script setup>
import {Search, Delete, DocumentAdd, Edit, Tools, RefreshRight} from '@element-plus/icons-vue'
import requestUtil, {getServerUrl} from "@/util/request";
import {ref} from 'vue'
import Dialog from './components/dialog'
import MenuDialog from './components/menuDialog.vue'
import {ElMessage, ElMessageBox} from 'element-plus'

const tableData = ref([])

const total = ref(0)

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

// 分配角色弹窗
const sysRoleList = ref([])
const menuDialogVisible = ref(false)

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
  const res = await requestUtil.post("role/delete", ids)
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
    initRoleList();
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
  }
}

const handleDialogValue = (roleId) => {
  if (roleId) {
    id.value = roleId;
    dialogTitle.value = "角色修改"
  } else {
    id.value = -1;
    dialogTitle.value = "角色添加"
  }
  dialogVisible.value = true
}

const initRoleList = async () => {
  if (!sessionStorage.getItem('token')) {
    console.log("无token，取消请求 role/list");
    return;
  }
  const res = await requestUtil.post("role/list", queryForm.value);
  tableData.value = res.data.roleList;
  total.value = res.data.total;
  console.log("tableData: " + tableData.value);
  console.log("total: " + total.value);

}
initRoleList();

// 每页记录数发生变化调用
const handleSizeChange = (pageSize) => {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = pageSize;
  initRoleList();
}

// 当前页码发生变化调用
const handleCurrentChange = (pageNum) => {
  queryForm.value.pageNum = pageNum;
  initRoleList();
}

// 重置密码
const handleResetPassword = async (id) => {
  const res = await requestUtil.get("role/resetPassword/" + id)
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
    initRoleList();
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
  }
}

const statusChangeHandle = async (row) => {
  let res = await
      requestUtil.get("role/updateStatus/" + row.id + "/status/" + row.status);
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
    initRoleList();
  }
}

const handleMenuDialogValue = (roleId) => {
  console.log("roleId=" + roleId)
  id.value = roleId;
  menuDialogVisible.value = true
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
</style>