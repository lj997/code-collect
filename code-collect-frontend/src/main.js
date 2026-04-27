import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './styles/index.css'
import 'highlight.js/styles/atom-one-dark.css'
import highlight from './directives/highlight'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.directive('highlight', highlight)

app.mount('#app')
