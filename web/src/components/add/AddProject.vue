<template>
  <div class="container">
        <div class="row justify-content-center mx-1">
            <div class="col-md-6 mt-4 py-4 px-3 rounded shadow bg-light">
                <i @click="goHome" class="fa-solid fa-arrow-left-long float-start text-secondary back"></i>
                <h3 class="text-center p-2 color-1">Ajouter project</h3>
                <hr>
                <div class="row g-3">
                    <div class="col-md-6 form-floating">
                        <input v-model="project.nom" type="text" class="form-control">
                        <label>Nom</label>
                    </div>
                    <div class="col-md-6 form-floating">
                        <select v-model="project.client" class="form-select form-select-md">
                            <option value=""></option>
                            <option v-for="cl in getClientsX" :key="cl._id" :value="cl.nom">{{cl.nom}}</option>
                        </select>
                        <label>Client</label>
                    </div>
                    <div class="col-xs-6 col-6 form-floating">
                        <input v-model="project.date_debut" type="date" class="form-control">
                        <label>Date Debut</label>
                    </div>
                    <div class="col-xs-6 col-6 form-floating">
                        <input v-model="project.date_fin" type="date" class="form-control">
                        <label>Date Fin</label>
                    </div>
                    <div class="col-xs-6 col-6  form-floating">
                        <select @change="toggleGarantie" v-model="project.type_commande" class="form-select form-select-md">
                            <option value="Marché">Marché</option>
                            <option value="Bon de commande">Bon de commande</option>
                            <option value="Marché cadre">Marché cadre</option>
                        </select>
                        <label>Type de commande</label>
                    </div>
                    <div class="col-xs-6 col-6 form-floating">
                        <select v-model="project.status" class="form-select form-select-md">
                            <option value=""></option>
                            <option v-for="st in status" :key="st" :value="st">{{st}}</option>
                        </select>
                        <label>Status</label>
                    </div>
                    <div class="col-md-6">
                        <div class="form-floating">
                            <input v-model="project.montant" type="number" class="form-control" min="0" step="100" id="floatingInput5" >
                            <label for="floatingInput5">Montant DH</label>
                        </div>
                    </div>
                    <div class="col-md-6">
                        <div v-if="show_garantie" class="form-floating">
                            <input v-model="project.garantie" type="number" class="form-control" min="0" step="100" id="floatingInput6">
                            <label for="floatingInput6">Garantie DH</label>
                        </div>
                    </div>
                </div>
                <div v-if="error!=''" class="col-md-12 alert alert-danger">{{error}}</div>
                <div class="float-end">
                    <i v-if="loading" class="fa-solid fa-circle-notch fa-spin mx-3"></i>
                    <button @click="addProject" class="btn btn-primary">Ajouter</button>
                </div>
            </div>
        </div>
  </div>
</template>

<script>
import {mapActions } from "vuex";
import Swal from 'sweetalert2'

export default {
    name:"AddProject",
    data(){
        return{
            project:{
                nom:null,
                client:null,
                date_debut:null,
                date_fin:null,
                status:null,
                type_commande:null,
                montant:0,
                garantie:0,
            },
            clients:[],
            error:'',
            loading:false,
            show_garantie:false,
            status:["Prospecter","En cours","Reception provisoir","Reception définitif","Cloturer"]
        }
    },
    methods:{
      ...mapActions(['getActions',"postActions"]),
        goHome(){
            this.$router.push("/")
        },
        addProject(){
            var p=this.project
            if(p.nom===""||p.client===""||p.date_debut===null||p.date_fin===""||p.status===""||p.montant===0){
                this.error="rempliser tous les champs"
                return
            }
            if(this.show_garantie){
                if(p.garantie===0){
                  this.error="ajouter Garantie"
                  return
                }
            }
            // check if date debut < date fin
            if(new Date(p.date_fin).setHours(0,0,0,0)-new Date(p.date_debut).setHours(0,0,0,0)<=0){
                this.error="Date fin inférieur à date debut"
                return
            }
            this.loading=true
            this.error=""
            this.postActions({path:'/api/projects',data:this.project})
            .then(()=>{
                this.loading=false
                Swal.fire('Projet Ajouté')
                this.goHome()
            })
            .catch(err=>{
                this.loading=false
                this.error=err.response.data
            })
        },
        toggleGarantie(){
            if(this.project.type_commande==="Bon de commande"){
                this.show_garantie=true
            }else{
                this.show_garantie=false
            }
        }
    },
    created(){
       this.getActions({path:'/api/clients'})
       .then(res=>this.clients=res.data)
       .catch(err=>console.log(err.response))
    }
}
</script>

<style>

</style>
