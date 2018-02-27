/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import Modal from 'material-ui/Modal';
import Button from 'material-ui/Button';
import FormControl from "material-ui/es/Form/FormControl";
import InputLabel from "material-ui/es/Input/InputLabel";
import Input from "material-ui/es/Input/Input";
import {getServer} from "../../resources/util";
import axios from "axios/index";
import Select from "material-ui/es/Select/Select";


function getModalStyle() {

    return {
        top: `${40}%`,
        left: `${40}%`,
    };
}

const styles = theme => ({
    paper: {
        position: 'absolute',
        width: theme.spacing.unit * 60,
        backgroundColor: theme.palette.background.paper,
        boxShadow: theme.shadows[5],
        padding: theme.spacing.unit * 4,
    },

    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    formControl: {
        margin: theme.spacing.unit,
    },
});

class ChangeBranchVersionModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            open: false,
            branchId:"",
            change:false,
            branchName:"",
            versionId:"",
            productId:"",
            versionList:[],
            branchList:[],
            repoId:"",
            repoName:""

        };
    }



    fetchVersionList = (id) => {
        let data = {
            productId : id
        };
        axios.post('http://'+getServer()+'/lts/products/versions',data
        ).then(
            (response) => {
                let datat = response.data;
                this.setState(
                    {
                        versionList: datat
                    }
                );
            }
        )
    };


    handleClose = () => {
        this.setState({ open: false });
    };


    componentWillUpdate(nextProps, nextState) {
        if (nextProps.open !== this.state.open) {
            this.setState({
                open : !this.state.open,
                change: nextProps.data.change,
                branchId: nextProps.data.branchId,
                branchName: nextProps.data.branchName,
                versionId: nextProps.data.versionId,
                productId: nextProps.productId,
                branchList: nextProps.branchList,
                repoId: nextProps.repoId,
                repoName: nextProps.repoName
            },
                ()=> this.fetchVersionList(this.state.productId)
            );
        }
    }

    addVersion = () => {
        if(this.validateName(this.state.branchName,this.state.branchList)) {
            let data = {
                branchName: this.state.branchName,
                versionId: this.state.versionId,
                repoId: this.state.repoId
            };

            axios.post('http://' + getServer() + '/lts/branches/versions/add', data
            ).then(
                (response) => {
                    let datat = response.data;
                    this.props.fetchBranches(this.state.repoId,this.state.repoName);
                    this.setState({
                        open: false,
                        branchName:"",
                        versionId:"",
                        productId:"",
                        versionList:[],
                        branchList:[],
                        repoId:"",
                        repoName:""
                    });
                }
            )
        }
    };

    changeVersion = () => {
        let data = {
            branchId: this.state.branchId,
            versionId: this.state.versionId
        };

        console.log(data);
        axios.post('http://' + getServer() + '/lts/branches/changeVersion', data
        ).then(
            (response) => {
                let datat = response.data;
                this.props.fetchBranches(this.state.repoId,this.state.repoName);
                this.setState({
                    open: false,
                    branchName:"",
                    versionId:"",
                    productId:"",
                    versionList:[],
                    branchList:[],
                    repoId:"",
                    repoName:""
                });

            }
        )
    };



    handleChange = name => event => {
        this.setState({ [name]: event.target.value });
    };



    validateName(name,branchNames){
        let isValid = true;
        if(name==""){
            isValid = false;
        } else{
            branchNames.forEach(function (branchesExisting) {
                if( branchesExisting["branchName"]==name && branchesExisting["branchId"]>0){
                    isValid = false;
                }
            })
        }

        return isValid;
    }

    render() {
        const { classes } = this.props;
        let buttonLabel = "Add Version";
        let callbackFunc = this.addVersion;
        if(this.state.change){
            buttonLabel = "Change";
            callbackFunc = this.changeVersion
        }

        return (
            <div>
                <Modal
                    aria-labelledby="simple-modal-title"
                    aria-describedby="simple-modal-description"
                    open={this.state.open}
                    onClose={this.handleClose}
                >
                    <div style={getModalStyle()} className={classes.paper}>
                        <div className={classes.container}>
                            <FormControl className={classes.formControl}>
                                <InputLabel htmlFor="age-native-simple">Version</InputLabel>
                                <Select
                                    native
                                    value={this.state.versionId}
                                    onChange={this.handleChange("versionId")}
                                    inputProps={{
                                        id: 'age-native-simple',
                                    }}
                                >
                                    <option value="" />
                                    {this.state.versionList.map((value,index)=>
                                        <option value={value["versionId"]} key={index}>{value["versionName"]}</option>
                                    )}
                                </Select>
                            </FormControl>
                            <FormControl className={classes.formControl}>
                                <Button onClick={callbackFunc}>{buttonLabel}</Button>
                                <Button onClick={this.handleClose}>Cancel</Button>
                            </FormControl>
                        </div>
                    </div>
                </Modal>
            </div>
        );
    }
}

ChangeBranchVersionModal.propTypes = {
    classes: PropTypes.object.isRequired,
};

// We need an intermediary variable for handling the recursive nesting.
const SimpleModalWrapped = withStyles(styles)(ChangeBranchVersionModal);

export default SimpleModalWrapped;