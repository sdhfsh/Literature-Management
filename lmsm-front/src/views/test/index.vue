<template>
<el-button type="danger" @click="testHandler">测试接口</el-button>
<el-button type="danger" @click="testLogoutHandler">测试logout接口</el-button>
</template>

<script setup>
import requestUtil from '@/util/request'
import {ElMessage} from "element-plus";


const testHandler=async ()=>{
  let result=await requestUtil.get("test/user/1");

}

const testLogoutHandler=async ()=>{
  let result=await requestUtil.get("/user/logout");
  let data = result.data;
  if (data.code == 200) {
    ElMessage.success(data.msg);
  } else {
    ElMessage.error(data.msg);
  }
  console.log(result.data);
}


</script>

<style scoped>

</style>
