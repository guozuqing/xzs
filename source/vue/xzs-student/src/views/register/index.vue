<template>
  <div class="login-container">
    <div class="login-card">
      <div class="title-container">
        <div class="logo-icon">
          <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg" width="48" height="48">
            <rect width="48" height="48" rx="12" fill="#4A90E2"/>
            <path d="M14 24L22 32L34 16" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <h3 class="title">天空领域</h3>
        <p class="subtitle">培训发展部考试系统</p>
      </div>

      <el-form ref="loginForm" :model="loginForm" auto-complete="on" label-position="left">
        <el-form-item prop="userName">
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>
          <el-input
            ref="userName"
            v-model="loginForm.userName"
            placeholder="请输入用户名"
            name="userName"
            type="text"
            tabindex="1"
            auto-complete="on"
          />
        </el-form-item>

        <el-form-item prop="password">
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <el-input
            ref="password"
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            name="password"
            tabindex="2"
            auto-complete="on"
            @keyup.enter.native="handleRegister"
          />
        </el-form-item>

        <el-form-item prop="userLevel">
          <span class="svg-container">
            <svg-icon icon-class="education" />
          </span>
          <el-select v-model="loginForm.userLevel" placeholder="请选择学科" class="select-input">
            <el-option v-for="item in subjects" :key="item.id" :value="item.id" :label="item.name"></el-option>
          </el-select>
        </el-form-item>

        <el-button :loading="loading" type="primary" class="login-btn" @click.native.prevent="handleRegister">
          {{ loading ? '注册中...' : '注 册' }}
        </el-button>
      </el-form>
    </div>

    <div class="account-foot-copyright">
      <span>Copyright &copy; 2019-2026 天空领域培训发展部 版权所有</span>
    </div>
  </div>
</template>

<script>
import { mapMutations } from 'vuex'
import registerApi from '@/api/register'
import subjectApi from '@/api/subject'

export default {
  name: 'Register',
  data () {
    return {
      loginForm: {
        userName: '',
        password: '',
        userLevel: null
      },
      subjects: [],
      loading: false
    }
  },
  created () {
    let _this = this
    subjectApi.list().then(re => {
      _this.subjects = re.response
    })
  },
  methods: {
    handleRegister () {
      let _this = this
      this.loading = true
      registerApi.register(this.loginForm).then(function (result) {
        if (result && result.code === 1) {
          _this.$router.push({ path: '/login' })
        } else {
          _this.loading = false
          _this.$message.error(result.message)
        }
      }).catch(function () {
        _this.loading = false
      })
    },
    ...mapMutations('user', ['setUserName'])
  }
}
</script>

<style lang="scss">
/* 覆盖 element-ui 输入框样式，适配浅色主题 */
.login-container .el-input {
  display: inline-block;
  height: 46px;
  width: 85%;

  input {
    background: transparent;
    border: 0;
    -webkit-appearance: none;
    appearance: none;
    border-radius: 0;
    padding: 12px 5px 12px 15px;
    color: #333;
    height: 46px;
    caret-color: #4A90E2;

    &::placeholder {
      color: #aab0b8;
    }

    &:-webkit-autofill {
      box-shadow: 0 0 0px 1000px #fff inset !important;
      -webkit-text-fill-color: #333 !important;
    }
  }
}

.login-container .el-form-item {
  border: 1px solid #e0e6ed;
  background: #fff;
  border-radius: 8px;
  color: #333;
  margin-bottom: 20px;
  transition: border-color 0.3s;

  &:focus-within {
    border-color: #4A90E2;
    box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.12);
  }
}

.login-container .el-form-item__error {
  padding-left: 50px;
}

.login-container .el-select {
  display: inline-block;
  width: 85%;

  .el-input__inner {
    background: transparent;
    border: 0;
    height: 46px;
    color: #333;
    padding-left: 15px;
  }
}
</style>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #e8f0fe 0%, #f0f4ff 50%, #e8f4fd 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;

  &::before {
    content: '';
    position: absolute;
    top: -80px;
    right: -80px;
    width: 320px;
    height: 320px;
    border-radius: 50%;
    background: rgba(74, 144, 226, 0.1);
  }

  &::after {
    content: '';
    position: absolute;
    bottom: -60px;
    left: -60px;
    width: 240px;
    height: 240px;
    border-radius: 50%;
    background: rgba(74, 144, 226, 0.08);
  }
}

.login-card {
  position: relative;
  z-index: 1;
  width: 420px;
  max-width: calc(100% - 40px);
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(74, 144, 226, 0.15), 0 2px 8px rgba(0,0,0,0.06);
  padding: 48px 48px 40px;
}

.title-container {
  text-align: center;
  margin-bottom: 36px;

  .logo-icon {
    margin-bottom: 16px;
  }

  .title {
    font-size: 26px;
    color: #1a2840;
    margin: 0 0 6px;
    font-weight: 700;
    letter-spacing: 2px;
  }

  .subtitle {
    font-size: 13px;
    color: #8a9bb0;
    margin: 0;
    letter-spacing: 1px;
  }
}

.svg-container {
  padding: 6px 5px 6px 15px;
  color: #aab0b8;
  vertical-align: middle;
  width: 30px;
  display: inline-block;
  transition: color 0.2s;
}

.select-input {
  vertical-align: middle;
}

.login-btn {
  width: 100%;
  height: 46px;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 4px;
  border-radius: 8px;
  margin-top: 8px;
  background: linear-gradient(90deg, #4A90E2 0%, #5ba3f0 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(74, 144, 226, 0.35);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 20px rgba(74, 144, 226, 0.45);
  }

  &:active {
    transform: translateY(0);
  }
}

.account-foot-copyright {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1;
  font-size: 12px;
  color: #9aaec4;
  text-align: center;
  white-space: nowrap;
}

@media only screen and (max-width: 480px) {
  .login-card {
    padding: 36px 24px 28px;
  }
}
</style>
