import pluginVue from 'eslint-plugin-vue'
import vueParser from 'vue-eslint-parser'

export default [
  {
    ignores: ['node_modules', 'dist', '**/*.local']
  },
  {
    files: ['**/*.{js,jsx,mjs,cjs,ts,tsx}'],
    languageOptions: {
      ecmaVersion: 2021,
      sourceType: 'module'
    }
  },
  {
    files: ['**/*.vue'],
    plugins: { vue: pluginVue },
    languageOptions: {
      parser: vueParser,
      ecmaVersion: 2021,
      sourceType: 'module'
    },
    rules: {
      ...pluginVue.configs['flat/recommended'].rules
    }
  }
]
