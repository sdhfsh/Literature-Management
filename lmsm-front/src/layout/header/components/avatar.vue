<template>
  <!-- 用户头像（个人中心、注销登录等） -->
  <el-dropdown>
    <span class="el-dropdown-link">
      <el-avatar shape="square" :size="40" :src="squareUrl"/>
      <!--      <el-avatar :size="40" :src="circleUrl"/>-->
      &nbsp;&nbsp;{{ currentUser.username }}
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item>个人中心</el-dropdown-item>
        <el-dropdown-item divided @click="logout">注销退出</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup>
import {ref} from 'vue'
import store from '@/store'
import requestUtil, {getServerUrl} from '@/util/request'
import {ElMessage} from "element-plus";

const currentUser = ref(store.getters.GET_USERINFO);
const squareUrl = ref(getServerUrl() + 'image/userAvatar/' + currentUser.value.avatar)

const logout = async () => {
  let result = await requestUtil.get("/user/logout");
  let data = result.data;
  if (data.code == 200) {
    store.dispatch('logout')
    ElMessage.success(data.msg);
  } else {
    ElMessage.error(data.msg);
  }
  console.log(result.data);
}

</script>

<style scoped lang="scss">
.el-dropdown-link {
  cursor: pointer;
  color: black;
  display: flex;
  align-items: center;
  outline: none;
  padding: 5px;
  border-radius: 5px;
  transition: background-color 0.3s ease, color 0.3s ease;
}

.el-dropdown-link:hover {
  background-color: rgba(0, 0, 0, 0.05); /* 轻微变暗 */
}

</style>