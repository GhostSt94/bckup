import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from "./store/index";
import PrimeVue from 'primevue/config';
import ToastService from 'primevue/toastservice';
import ConfirmationService from 'primevue/confirmationservice';
import "primevue/resources/themes/saga-blue/theme.css"
import "primevue/resources/primevue.min.css"
import "primeicons/primeicons.css"
import "./style.css";
import axios from "axios"

const url=store.getters.getApiUrl
let id=localStorage.getItem("session_id")
axios.post(`${url}/auth/check`,{session_id:id===null?"":id})
.then(res => {
    if(res.status===200){
        if(router.currentRoute.value.name==="Auth"){
            console.log("Logged in");
            router.push('/')
        }
    }else{
        console.log("not Logged in");
        router.push('/auth')
    }
})
.catch(err => {
    console.log(err.response);
    router.push('/auth')
})


createApp(App).use(router).use(store).use(PrimeVue).use(ToastService).use(ConfirmationService).mount('#app')
