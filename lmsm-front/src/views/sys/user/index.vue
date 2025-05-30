<template>
  <div class="app-container">
    <el-row :gutter="20" class="header">
      <el-col :span="7">
        <el-input placeholder="请输入用户名..." v-model="queryForm.query" clearable
        ></el-input>
      </el-col>
      <el-button type="primary" :icon="Search" @click="initUserList">搜索</el-button>
      <el-button type="success" :icon="DocumentAdd" @click="handleDialogValue()">新增</el-button>
      <el-popconfirm title="您确定批量删除这些记录吗？" @confirm="handleDelete(null)">
        <template #reference>
          <el-button type="danger" :disabled="delBtnStatus" :icon="Delete">批量删除</el-button>
        </template>
      </el-popconfirm>
    </el-row>
    <el-table :data="tableData" stripe style="width: 100%" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55"/>
      <el-table-column prop="avatar" label="头像" width="80" align="center">
        <template v-slot="scope">
          <img :src="getServerUrl()+'image/userAvatar/'+scope.row.avatar"
               width="50" height="50"/>
        </template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" width="100"
                       align="center"/>
      <el-table-column prop="roles" label="拥有角色" width="200" align="center">
        <template v-slot="scope">
          <el-tag size="small" type="warning" v-for="item in scope.row.roleList">
            {{ item.name }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="email" label="邮箱" width="200" align="center"/>
      <el-table-column prop="phone" label="手机号" width="120"
                       align="center"/>
      <el-table-column prop="status" label="状态？" width="200" align="center"
      >
        <template v-slot="{row}">
          <el-switch v-model="row.status" @change="statusChangeHandle(row)"
                     active-text="正常"
                     inactive-text="禁用" active-value="0" inactive-value="1">
          </el-switch>
        </template>
      </el-table-column>
      <el-table-column prop="createAt" label="创建时间" width="200" align="center">
<!--        <template #default="scope">-->
<!--          {{ formatDate(scope.row.createdAt) }}-->
<!--        </template>-->
      </el-table-column>
      <el-table-column prop="loginDate" label="最后登录时间" width="200"
                       align="center"/>
      <el-table-column prop="remark" label="备注"/>
      <el-table-column prop="action" label="操作" width="400" fixed="right"
                       align="center">
        <template v-slot="scope">
          <el-button type="primary" :icon="Tools" @click="handleRoleDialogValue(scope.row.id,scope.row.roleList)">
            分配角色
          </el-button>
          <el-button v-if="scope.row.username!=='machaoyue'" type="primary" :icon="Edit"
                     @click="handleDialogValue(scope.row.id)"/>
          <el-popconfirm v-if="scope.row.username!='machaoyue'" title="您确定要删除这条记录吗？"
                         @confirm="handleDelete(scope.row.id)">
            <template #reference>
              <el-button type="danger" :icon="Delete"/>
            </template>
          </el-popconfirm>
          <el-popconfirm v-if="scope.row.username!='machaoyue'" title="您确定要对这个用户重置密码吗？"
                         @confirm="handleResetPassword(scope.row.id)">
            <template #reference>
              <el-button type="warning" :icon="RefreshRight">重置密码</el-button>
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
  <Dialog v-model="dialogVisible" :id="id" :dialogTitle="dialogTitle" @initUserList="initUserList"/>
  <RoleDialog v-model="roleDialogVisible" :sysRoleList="sysRoleList" :id="id" :roleDialogVisible="roleDialogVisible"
              @initUserList="initUserList">
  </RoleDialog>
</template>

<script setup>
import {Search, Delete, DocumentAdd, Edit, Tools, RefreshRight} from '@element-plus/icons-vue'
import requestUtil, {getServerUrl} from "@/util/request";
import {ref} from 'vue'
import Dialog from './components/dialog'
import RoleDialog from './components/roleDialog'
import {ElMessage, ElMessageBox} from 'element-plus'
import {formatDate} from "@/util/formatDate";

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
const roleDialogVisible = ref(false)

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
  const res = await requestUtil.post("user/delete", ids)
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
    initUserList();
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
  }
}

const handleDialogValue = (userId) => {
  if (userId) {
    id.value = userId;
    dialogTitle.value = "用户修改"
  } else {
    id.value = -1;
    dialogTitle.value = "用户添加"
  }
  dialogVisible.value = true
}

const initUserList = async () => {
  if (!sessionStorage.getItem('token')) {
    console.log("无token，取消请求 user/list");
    return;
  }
  const res = await requestUtil.post("user/list", queryForm.value);
  tableData.value = res.data.userList;
  total.value = res.data.total;
  console.log("tableData: " + tableData.value);
  console.log("total: " + total.value);

}
initUserList();

// 每页记录数发生变化调用
const handleSizeChange = (pageSize) => {
  queryForm.value.pageNum = 1;
  queryForm.value.pageSize = pageSize;
  initUserList();
}

// 当前页码发生变化调用
const handleCurrentChange = (pageNum) => {
  queryForm.value.pageNum = pageNum;
  initUserList();
}

// 重置密码
const handleResetPassword = async (id) => {
  const res = await requestUtil.get("user/resetPassword/" + id)
  if (res.data.code == 200) {
    ElMessage({
      type: 'success',
      message: '执行成功!'
    })
    initUserList();
  } else {
    ElMessage({
      type: 'error',
      message: res.data.msg,
    })
  }
}

const statusChangeHandle = async (row) => {
  let res = await
      requestUtil.get("user/updateStatus/" + row.id + "/status/" + row.status);
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
    initUserList();
  }
}

const handleRoleDialogValue = (userId, roleList) => {
  console.log("userId=" + userId)
  id.value = userId;
  sysRoleList.value = roleList;
  roleDialogVisible.value = true
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