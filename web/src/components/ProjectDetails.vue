<template>

    <i v-if="loading" class="fa-solid fa-circle-notch fa-spin mx-3 fa-2xl position-absolute start-50 top-50 translate-middle"></i>
    <div v-else class="container-fluid">
        <div v-if="not_found" class="position-absolute start-50 top-50 translate-middle text-center">
            <h2 class="text-muted">NOT FOUND</h2>
            <router-link to="/">Home</router-link>
        </div>
        <div v-else class="container pt-5">
            <div class="row justify-content-between mb-4">
                <div class="col-md-8 bg-white py-3 rounded shadow-sm">
                    <i @click="goHome" class="fa-solid fa-arrow-left-long float-start text-secondary back"></i>
                    <div class="float-end">
                        <i v-if="!updating" @click="toggleUpdate" class="fa-solid fa-pen-to-square fa-lg text-warning m-3"></i>
                        <i v-else @click="toggleUpdate" class="fa-solid fa-xmark fa-lg m-3"></i>
                        <i @click='deleteProject' class="fa-solid fa-trash-can fa-lg text-danger m-3"></i>
                    </div>
                    <div class="row justify-content-center text-center">
                        <div class="col-6 mb-3">
                            <span class="text-muted"><i>Name</i> </span><br>
                            <input v-if="updating" type="text" class="form-control" v-model="project.nom">
                            <span v-else class="h5 fw-bold">{{project.nom}}</span>
                        </div>
                        <div class="col-6 mb-3">
                            <span class="text-muted"><i>Client</i> </span><br>
                            <input v-if="updating" type="text" class="form-control" v-model="project.client">
                            <span v-else class="h5 fw-bold">{{project.client}}</span>
                        </div>
                        <div class="col-12 mb-3 date">
                            <table class="table table-borderless">
                                <tbody>
                                    <tr class="text-muted">
                                        <td><i>Date Debut</i> </td>
                                        <td></td>
                                        <td><i>Date Fin</i> </td>
                                    </tr>
                                    <tr class="fw-bold">
                                        <td>
                                            <input v-if="updating" type="date" class="form-control" v-model="project.date_debut">
                                            <span v-else>{{project.date_debut}}</span>
                                        </td>
                                        <td><i class="fa-solid fa-arrow-right-long"></i></td>
                                        <td>
                                            <input v-if="updating" type="date" class="form-control" v-model="project.date_fin">
                                            <span v-else>{{project.date_fin}}</span>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div class="col-6 mb-3">
                            <span class="text-muted"><i>Type de commande</i> </span><br>
                            <select @change="toggleGarantie" v-if="updating" v-model="project.type_commande" class="form-select form-select-md" aria-label=".form-select-sm">
                                <option value="Marché">Marché</option>
                                <option value="Bon de commande">Bon de commande</option>
                                <option value="Marché cadre">Marché cadre</option>
                            </select>
                            <span v-else-if="project.type_commande" class="h5 fw-bold">{{project.type_commande}}</span>
                        </div>
                        <div class="col-6 mb-3">
                            <span class="text-muted"><i>Status</i> </span><br>
                            <select v-if="updating" v-model="project.status" class="form-select form-select-md" aria-label=".form-select-sm">
                                <option value="Prospecter">Prospecter</option>
                                <option value="En cours">En cours</option>
                                <option value="Reception provisoir">Reception provisoir</option>
                                <option value="Reception définitif">Reception définitif</option>
                                <option value="Cloturer">Cloturer</option>
                            </select>
                            <span v-else class="h5 fw-bold">{{project.status}}</span>
                        </div>
                        <hr class="my-3">
                        <div class="col-6 mb-3">
                            <span class="text-muted"><i>Montant</i> </span><br>
                            <input v-if="updating" type="number" min="0" step="100" class="form-control" v-model="project.montant">
                            <span v-else class="h5 fw-bold">{{project.montant}} DH</span>
                        </div>
                        <div v-if="show_garantie" class="col-6 mb-3">
                            <span class="text-muted"><i>Garantie</i> </span><br>
                            <input v-if="updating && show_garantie" type="number" min="0" step="100" class="form-control" v-model="project.garantie">
                            <span v-else class="h5 fw-bold">{{project.garantie}} DH</span>
                        </div>
                    </div>
                    <button v-if="updating" @click="updateProject" class="btn btn-warning float-end">Modifier</button>
                </div>
                <div class="col-md-3 mt-3 bg-white py-3 rounded shadow-sm text-center h-50 factures">
                    <button type="button" class="btn btn-outline-primary border-0 float-end" data-bs-toggle="modal" data-bs-target="#ProjectFileModal">
                        <i class="fa-solid fa-plus fa-lg"></i>
                    </button>
                    <h4>File</h4>
                    <hr>
                    <h6 v-if="project.file==null" class="text-muted">Aucun File</h6>
                    <div v-else class="container text-center">
                    <i @click="deleteProjectFile" title="supprimer fichier" class="fa-solid fa-xmark text-muted float-end opacity-25"></i>
                        <a v-bind:href="getApiUrl+'/api/files/project/'+project.file" target="_blank">
                            {{project.file.substring(project.file.indexOf("_") + 1)}}<i class="fa-solid fa-file-pdf ms-2"></i>
                        </a>
                    </div>
                </div>
            </div>
            <div class="row justify-content-center text-center mb-3">
                <div class="col-12 bg-white rounded shadow p-3">
                    <button type="button" class="btn btn-outline-primary border-0 m-1 float-end" data-bs-toggle="modal" data-bs-target="#factureModal">
                        <i class="fa-solid fa-plus fa-lg"></i>
                    </button>
                    <h4>Factures</h4><hr>
                    <h6 v-if="factures.length===0" class="text-muted">Aucune Facture</h6>
                    <table v-else class="table table-striped">
                        <thead>
                            <tr>
                                <td>Date</td>
                                <td>Montant</td>
                                <td>Status</td>
                                <td>File</td>
                                <td>
                                </td>
                            </tr>
                        </thead>
                        <tbody v-if="error_facture!=''">{{error_facture}}</tbody>
                        <tbody v-else>
                            <tr v-for="facture in factures" :key="facture._id" class="facture" :id="facture._id">
                                <td class="text-success fw-bold">{{facture.date}}</td>
                                <td>{{facture.montant}} DH</td>
                                <td class="text-center">
                                    <select v-if="updating_facture_id===facture._id" v-model="new_facture_status" class="form-select form-select-sm w-75" aria-label="status">
                                        <option value="prévu">Prévu</option>
                                        <option value="envoyer">Envoyer</option>
                                        <option value="payer">Payer</option>
                                    </select>
                                    <span v-else>{{facture.status}}</span>
                                </td>
                                <td>
                                    <a v-if="facture.file!=null" v-bind:href="getApiUrl+'/api/files/facture/'+facture.file" target="_blank">
                                        <i class="fa-solid fa-file-pdf"></i>
                                    </a>
                                    <span v-else>...</span>
                                </td>
                                <td>
                                    <div v-if="updating_facture_id===facture._id">
                                        <i  @click="(e)=>toggleUpdateFacture(e,false)" class="fa-solid fa-xmark text-muted opacity-25"></i>
                                        <i  @click="updateFacture(facture._id)" class="fa-solid fa-check opacity-25 ms-2"></i>
                                    </div>
                                    <div v-else>
                                        <i @click="(e)=>toggleUpdateFacture(e,true)" class="fa-solid fa-pen-to-square text-warning opacity-25"></i>
                                        <i @click="(e)=>deleteFacture(e,facture._id)" title="supprimer facture" class="fa-solid fa-xmark text-muted opacity-25 ms-2"></i>
                                    </div>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div><Toast /><ConfirmDialog />
    <FactureModal @get-factures="getFacturesAll"/>
    <ProjectFileModal @get-project="getProject"/>
</template>

<script>
import FactureModal from "./project-details/FactureModal.vue";
import ProjectFileModal from "./project-details/ProjectFileModal.vue";
import Toast from 'primevue/toast';
import ConfirmDialog from 'primevue/confirmdialog';
import {mapGetters,mapActions} from "vuex"

export default {
    name:"ProjectDetails",
    components:{
        FactureModal,ProjectFileModal,Toast,ConfirmDialog
    },
    data(){
        return{
            project:{
                _id:this.$route.params.id,
                nom:"",
                client:"",
                date_debut:null,
                date_fin:null,
                status:"",
                type_commande:"",
                montant:0,
                garantie:0,
                file:null
            },
            loading:true,
            not_found:false,
            updating:false,
            updating_facture_id:null,
            factures:[],
            new_facture_status:null,
            // files:[],
            show_garantie:null,
            error:'',
            error_facture:''
        }
    },
    computed:mapGetters(["getApiUrl"]),
    methods:{
      ...mapActions(["deleteActions","putActions","getActions"]),
        goHome(){
            this.$router.push("/")
        },
        toggleUpdate(){
            this.updating=!this.updating
        },
        toggleUpdateFacture(event,bool){
            if(bool){
                this.updating_facture_id=event.target.closest('.facture').id
            }else{
                this.updating_facture_id=null
            }
        },
        // display garantie field if type commande is bon de commande
        toggleGarantie(){
            if(this.project.type_commande==="Bon de commande"){
                this.show_garantie=true
            }else{
                this.show_garantie=false
            }
        },
        toastSuccess(msg){
            this.$toast.add({severity:'success', summary: msg, life: 2000})
        },
        toastError(err){
            this.$toast.add({severity:'error', summary:"Error",detail: err, life: 2000})
        },
        //
        deleteProject(){
            this.$confirm.require({
                message:'Suprrimer ce projet ?',
                header: 'Confirmation',
                icon:"pi pi-exclamation-triangle",
                accept:()=>{
                  this.deleteActions({path:`/api/projects/${this.project._id}`})
                  .then(res=>{
                      console.log(res);
                      this.toastSuccess('Projet Supprimé')
                      this.$router.push("/")
                  })
                  .catch(err=>{
                      this.toastError(err)
                  })
                }
            })

        },
        updateProject(){
            this.$confirm.require({
                message:'Modifier ce projet ?',
                header: 'Confirmation',
                icon:"pi pi-exclamation-triangle",
                accept:()=>{
                    var p=this.project
                    if(p.nom===""||p.client===""||p.date_debut===null||p.date_fin===null||p.status===''||p.type_commande===""||p.montant===""){
                        this.toastError("Veuillez remplir tous les champs!")
                        return
                    }
                    if(!this.show_garantie){
                        this.project.garantie=null
                    }else{
                        this.project.garantie===null?this.project.garantie=0:null
                    }
                    this.putActions({path:`/api/projects/${this.project._id}`,data:this.project})
                    .then(()=>{
                        this.updating=false;
                        this.toastSuccess('Projet modifié')
                        this.getProject()
                    })
                    .catch(err=>{
                        this.toastError(err)
                    })
                }
            })
        },
        updateFacture(id){
            if(this.new_facture_status==null||this.new_facture_status==""){
              this.toastError("status is null")
              this.updating_facture_id=null
              return
            }
            this.putActions({path:`/api/factures/${id}`,data:{status:this.new_facture_status}})
            .then(()=>{
              this.toastSuccess('Facture Modifié')
              this.updating_facture_id=null
              this.getFacturesAll()
            })
            .catch(err=>{
              console.log(err.response);
              this.updating_facture_id=null
              this.toastError(err)
            })
        },
        deleteFacture(e,id){
          this.$confirm.require({
            message: 'Modifier ce projet ?',
            header: 'Confirmation',
            icon: "pi pi-exclamation-triangle",
            accept: () => {
              this.deleteActions({path:`/api/factures/${id}`})
                .then(() => {
                  this.toastSuccess('Facture supprimé')
                  e.target.closest('tr').remove()
                })
                .catch(err => {
                  this.toastError(err)
                })
            }
          })
        },
        deleteProjectFile(){
          this.deleteActions({path:`/api/projects/${this.project._id}/file`})
            .then(()=>{
                this.project.file=null
                this.toastSuccess('Fichier Supprimé')
            })
            .catch(err=>{
                this.toastError(err)
            })
        },
        getProject(){
            this.getActions({path:`/api/projects/${this.project._id}`})
            .then(res=>{
                this.loading=false
                this.project=res.data
                this.toggleGarantie()
            })
            .catch(err=>{
                this.loading=false
                this.not_found=true
                console.log(err.response);
            })
        },
        getFacturesAll(){
          this.getActions({path:`/api/factures/project/${this.project._id}`})
            .then(res=>{
                this.factures=res.data
            })
            .catch(err=>{
                this.error_facture=err.response.data
            })
        }
    },
    created(){
        this.getProject()
        this.getFacturesAll()
    }
}
</script>

<style scoped>
    .list-group{
        max-height: 250px;
        overflow-y: scroll;
    }
    .opacity-25:hover{
        opacity: 1 !important;
    }
    .date{
        cursor: default;
    }
    hr{color: gray;padding: 0;}
</style>
