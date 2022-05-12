import { createStore } from 'vuex'
import axios from "axios";

export default createStore({
    state:{
        API_URL:location.origin,
    },
    getters:{
        getApiUrl:(state)=>state.API_URL,
    },
    actions:{
        // async logout({getters}){
        //     let session_id=localStorage.getItem("session_id")
        //     await axios.post(`${getters.getApiUrl}/auth/logout`,{session_id:session_id==null?'':session_id})
        //         .catch(err=>console.log(err))
        //         .finally(localStorage.clear())
        // },
        // login({getters},data){
        //     //let url=getters.getApiUrl
        //     axios.post(`${getters.getApiUrl}/auth/login`,data)
        //     .then(res=>{
        //         localStorage.setItem('session_id',res.data)
                
        //     })
        //     .catch(err=>console.log(err))
        // },
        checkAPI(_,path){
            if(path.startsWith("/api")){
                return true
            }
            return false
        },
        getActions({getters,dispatch}, {path}){
            const res=dispatch('checkAPI',path)
            const id=localStorage.getItem('session_id')
            const headers=res&&id!==null?{"Authorization":id}:{}
            return axios.get(getters.getApiUrl+path,{headers});
        },
        postActions({getters,dispatch},{path,data}){
            const res=dispatch('checkAPI',path)
            const id=localStorage.getItem('session_id')
            const headers=res&&id!==null?{"Authorization":id}:{}
            return axios.post(getters.getApiUrl+path,data,{headers})
        },
        putActions({getters,dispatch},{path,data}){
            const res=dispatch('checkAPI',path)
            const id=localStorage.getItem('session_id')
            const headers=res&&id!==null?{"Authorization":id}:{}
            return axios.put(getters.getApiUrl+path,data,{headers})
        },
        deleteActions({getters,dispatch},{path}){
            const res=dispatch('checkAPI',path)
            const id=localStorage.getItem('session_id')
            const headers=res&&id!==null?{"Authorization":id}:{}
            return axios.delete(getters.getApiUrl+path,{headers})
        },
    },
})



