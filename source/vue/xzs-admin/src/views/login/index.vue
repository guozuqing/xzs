<template>
  <div class="login-container">
    <img src="@/assets/logo2.png" alt="banner" class="banner-bg" />
    <div class="login-overlay">
      <div class="login-form-wrap">
        <div class="title-container">
          <div class="logo-icon">
            <img src="@/assets/sky-logo.png" alt="天空领域" class="logo-img" />
          </div>
          <p class="subtitle">培训发展部管理系统</p>
        </div>

        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" auto-complete="on" label-position="left">
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

          <el-tooltip v-model="capsTooltip" content="Caps lock is On" placement="right" manual>
            <el-form-item prop="password">
              <span class="svg-container">
                <svg-icon icon-class="password" />
              </span>
              <el-input
                :key="passwordType"
                ref="password"
                v-model="loginForm.password"
                :type="passwordType"
                placeholder="请输入密码"
                name="password"
                tabindex="2"
                auto-complete="on"
                @keyup.native="checkCapslock"
                @blur="capsTooltip = false"
                @keyup.enter.native="handleLogin"
              />
              <span class="show-pwd" @click="showPwd">
                <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
              </span>
            </el-form-item>
          </el-tooltip>

          <el-button :loading="loading" type="primary" class="login-btn" @click.native.prevent="handleLogin">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form>

        <div class="login-copyright">
          <span>Copyright © 2019-2026 天空领域培训发展部 版权所有</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapMutations } from 'vuex'
import loginApi from '@/api/login'

export default {
  name: 'Login',
  data () {
    const validateUsername = (rule, value, callback) => {
      if (value.length < 5) {
        callback(new Error('用户名不能少于5个字符'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 5) {
        callback(new Error('密码不能少于5个字符'))
      } else {
        callback()
      }
    }
    return {
      loginForm: {
        userName: '',
        password: '',
        remember: true
      },
      loginRules: {
        userName: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      passwordType: 'password',
      capsTooltip: false,
      loading: false
    }
  },
  mounted () {
    if (this.loginForm.userName === '') {
      this.$refs.userName.focus()
    } else if (this.loginForm.password === '') {
      this.$refs.password.focus()
    }
  },
  methods: {
    checkCapslock ({ shiftKey, key } = {}) {
      if (key && key.length === 1) {
        // eslint-disable-next-line no-mixed-operators
        if (shiftKey && (key >= 'a' && key <= 'z') || !shiftKey && (key >= 'A' && key <= 'Z')) {
          this.capsTooltip = true
        } else {
          this.capsTooltip = false
        }
      }
      if (key === 'CapsLock' && this.capsTooltip === true) {
        this.capsTooltip = false
      }
    },
    showPwd () {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin () {
      let _this = this
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          loginApi.login(this.loginForm).then(function (result) {
            if (result && result.code === 1) {
              _this.setUserName(_this.loginForm.userName)
              _this.$router.push({ path: '/' })
            } else {
              _this.loading = false
              _this.$message({
                message: result.message,
                type: 'error'
              })
            }
          }).catch(function () {
            _this.loading = false
          })
        } else {
          return false
        }
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
  height: 52px;
  width: 85%;

  input {
    background: transparent;
    border: 0;
    -webkit-appearance: none;
    appearance: none;
    border-radius: 0;
    padding: 14px 5px 14px 15px;
    color: #333;
    height: 52px;
    font-size: 16px;
    caret-color: #4A90E2;

    &::placeholder {
      color: #aab0b8;
      font-size: 15px;
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
  margin-bottom: 24px;
  transition: border-color 0.3s;

  &:focus-within {
    border-color: #4A90E2;
    box-shadow: 0 0 0 3px rgba(74, 144, 226, 0.12);
  }
}

.login-container .el-form-item__error {
  padding-left: 50px;
}
</style>

<style lang="scss" scoped>
.login-container {
  position: relative;
  height: 100vh;
  width: 100%;
  overflow: hidden;
}

.banner-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}

.login-overlay {
  position: relative;
  z-index: 1;
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 40px 120px 40px 40px;
}

.login-form-wrap {
  width: 500px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border-radius: 16px;
  padding: 52px 44px 48px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.18), 0 2px 8px rgba(0, 0, 0, 0.08);
}

.title-container {
  text-align: center;
  margin-bottom: 36px;

  .logo-icon {
    margin-bottom: 16px;

    .logo-img {
      height: 60px;
      object-fit: contain;
    }
  }

  .subtitle {
    font-size: 16px;
    color: #8a9bb0;
    margin: 0;
    letter-spacing: 2px;
  }
}

.svg-container {
  padding: 6px 5px 6px 15px;
  color: #aab0b8;
  vertical-align: middle;
  width: 34px;
  font-size: 18px;
  display: inline-block;
  transition: color 0.2s;
}

.show-pwd {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 16px;
  color: #aab0b8;
  cursor: pointer;
  user-select: none;
  transition: color 0.2s;

  &:hover {
    color: #4A90E2;
  }
}

.login-btn {
  width: 100%;
  height: 52px;
  font-size: 18px;
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

.login-copyright {
  margin-top: 32px;
  text-align: center;
  font-size: 13px;
  color: #9aaec4;
  white-space: nowrap;
}

@media only screen and (max-width: 768px) {
  .login-overlay {
    justify-content: center;
    padding: 24px;
  }

  .login-form-wrap {
    width: 100%;
    max-width: 500px;
  }
}
</style>
