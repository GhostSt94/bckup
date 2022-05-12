<template>
    <div class="container pt-4">
        <i @click="goHome" class="fa-solid fa-arrow-left-long float-start text-secondary back m-2"></i>
        <h4 class="ms-5 color-1">Liste des Clients <i class="fa-solid fa-plus btn btn-primary rounded-pill ms-1" @click="goToAddPage"></i></h4>
        <div class="container">
            <div class="input-group filter m-2 my-4 m-auto search">
                <span class="input-group-text">Nom</span>
                <input @input="filterClients" type="text" class="form-control" v-model="client_filter" placeholder="Tapez un nom">
            </div>
        </div>
        <i v-if="loading" class="fa-solid fa-circle-notch fa-spin mx-3 fa-2xl position-absolute start-50 top-50 translate-middle"></i>
        <div v-else-if="error!==''">
            <h6 class="text-danger text-center">{{error}}</h6>
        </div>
        <div  v-else-if="clients.length>0" class="row justify-content-center pt-3">
            <div @click="goToDetails(cl._id)" v-for="cl in clients" :key="cl._id" class="col-3 m-2 p-2 bg-white rounded shadow-sm client">
                <Avatar :label="cl.nom[0]" /><span class="ms-2">{{cl.nom}}</span>
            </div>
        </div>
        <div  v-else class="text-center">
            <h4 class="text-muted m-3">Aucun Projet</h4>
        </div>
    </div>
</template>

<script>
import { mapActions } from "vuex";
import Avatar from 'primevue/avatar';
export default {
    name:"ListeClients",
    components:{
        Avatar
    },
    data(){
        return{
            clients_all:[],
            clients:[],
            client_filter:null,
            loading:false,
            error:""
        }
    },
    methods:{
        goHome(){
            this.$router.push("/")
        },
        goToAddPage(){
            this.$router.push("/ajouter/client")
        },
        goToDetails(id){
            this.$router.push(`/clients/${id}`)
        },
        filterClients(){
            try {
                this.clients=this.clients_all.filter(cl=>cl.nom.toLowerCase().startsWith(this.client_filter.toLowerCase()))
            } catch (error) {
                this.error="Error Filtering"
            }
        },
        ...mapActions(['getActions'])
    },
    created(){
        this.loading=true
        this.getActions({path:"/api/clients"})
          .then(res=> {
            this.clients_all=res.data
            this.clients = res.data;
            this.loading=false
          })
          .catch(err=>{
            console.log(err.response)
            this.error="Cannot find Client list";
            this.loading=false
          })
        }
}
</script>

<style scoped>
    .client{
        color:grey;
    }
    .client:hover{
        cursor: pointer;color: rgb(239, 107, 41);font-weight: bold;
    }
    .search input:focus{
        box-shadow: 1px 1px 5px rgb(239, 107, 41);
    }
    .filter{width: 40%;}
    @media screen and (max-width: 700px){
        .filter{width: 80%;}
    }
</style>
