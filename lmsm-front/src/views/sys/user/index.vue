<template>
  <div class="app-container">
    <el-table :data="tableData" stripe style="width: 100%">
      <el-table-column prop="username" label="用户名" width="180"/>
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
</template>

<script setup>
import requestUtil, {getServerUrl} from "@/util/request";
import {ref} from 'vue'

const tableData = ref([])

const total = ref(0)

const queryForm = ref({
  query: '',
  pageNum: 1,
  pageSize: 1
})

const initUserList = async () => {
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
</script>

<style scoped lang="scss">
.header{
  padding-bottom: 16px;
  box-sizing: border-box;
}
.el-pagination{
  float: right;
  padding: 20px;
  box-sizing: border-box;
}
::v-deep th.el-table__cell{
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