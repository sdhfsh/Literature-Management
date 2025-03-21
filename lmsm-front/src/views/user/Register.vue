<template>
  <div class="login">
    <el-form ref="registerRef" :model="registerForm" :rules="registerRules" class="login-form">
      <h3 class="title">文献管理系统</h3>
      <el-form-item prop="username">
        <el-input v-model="registerForm.username" type="text" size="large" auto-complete="off" placeholder="账号">
          <template #prefix>
            <svg-icon icon="user"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="registerForm.password" type="password" size="large" auto-complete="off" placeholder="密码"
                  @keyup.enter="handleRegister">
          <template #prefix>
            <svg-icon icon="password"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="registerForm.email" type="text" size="large" auto-complete="off" placeholder="邮箱"
                  @keyup.enter="handleRegister">
          <template #prefix>
            <svg-icon icon="email"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="phone">
        <el-input v-model="registerForm.phone" type="text" size="large" auto-complete="off" placeholder="手机号"
                  @keyup.enter="handleRegister">
          <template #prefix>
            <svg-icon icon="phone"/>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button size="large" type="primary" style="width:100%;" @click.prevent="handleRegister">
          <span>注 册</span>
        </el-button>
        <p class="login-text">
          已有账号，<el-link type="primary" @click="$router.push('/login')">立即登录</el-link>
        </p>
      </el-form-item>
    </el-form>
    <div class="el-login-footer">
      <span>Copyright © 2013-2022 <a href="https://www.baidu.com" target="_blank">machaoyue</a> 版权所有.</span>
    </div>
  </div>
</template>

<script setup>
import {ref} from "vue";
import requestUtil from "@/util/request"
import store from "@/store"
import {ElMessage} from "element-plus";
import router from "@/router";

const registerRef = ref(null);

const registerForm = ref({
  username: "",
  password: "",
  email: "",
  phone: ""
});

const registerRules = ref({
  username: [{required: true, message: "请输入账号", trigger: "blur"}],
  password: [{required: true, message: "请输入密码", trigger: "blur"}],
  email: [{required: true, message: "请输入邮箱", trigger: "blur"}],
  phone: [{required: true, message: "请输入手机号", trigger: "blur"}]
});

const handleRegister = () => {
  registerRef.value.validate(async (valid) => {
    if (valid) {
      let result = await requestUtil.post("/user/register", JSON.stringify(registerForm.value));
      let data = result.data;
      if (data.code == 200) {
        router.replace("/login")
        ElMessage.success(data.msg + ", 请登录~");
      } else {
        ElMessage.error(data.msg);
      }
      console.log(result.data);
    } else {
      console.log("验证失败");
    }
  })
};
</script>

<style lang="scss" scoped>
a {
  color: white;
}

.login {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../../assets/images/login-background.png");
  background-size: cover;
}

.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.login-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;

  .el-input {
    height: 40px;

    input {
      display: inline-block;
      height: 40px;
    }
  }
}

.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

.login-text {
  display: flex;
  align-items: center;
  margin-top: 20px;
  font-size: 14px;
  color: #787878;
  .el-link {
    color: #409EFF; // 设置成可见的颜色
  }
}
</style>
