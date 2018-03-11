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
import { FormControl } from 'material-ui/Form';
import Input, { InputLabel } from 'material-ui/Input';
import {getServer} from "../../resources/util";
import axios from "axios/index";
import Select from 'material-ui/Select';
import AppBar from "material-ui/AppBar";
import Toolbar from "material-ui/Toolbar";
import Typography from "material-ui/Typography";
import IconButton from 'material-ui/IconButton';
import CloseIcon from 'material-ui-icons/Close';
import { MenuItem } from 'material-ui/Menu';



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
    },

    container: {
        display: 'flex',
        flexWrap: 'wrap',
    },
    inputSelect: {
        width: `50%`,
        paddingLeft:`5%`,
        marginTop: 10,
        marginBottom: 10
    },
    appBar: {
        marginLeft: 0,
        marginRight: 0,
        paddingLeft: 0,
        paddingRight: 0
    },
    buttonList: {
        width:`40%`,
        display: 'inline-block',
        marginTop: 15,
        marginBottom: 10
    },
    closeButton : {
        float: 'right'
    },
    flex: {
        flex: 1,
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
            repoUrl:"",
            repoName:"",
            repoId:""

        };
    }



    fetchVersionList = (productId) => {
        let data = {
            productId : productId
        };
        axios.post(getServer()+'/lts/products/versions',data
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
                repoUrl: nextProps.repoUrl,
                repoName: nextProps.repoName,
                repoId: nextProps.repoId
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

            axios.post(getServer() + '/lts/branches/versions/add', data
            ).then(
                (response) => {
                    let datat = response.data;
                    this.props.fetchBranches(this.state.repoUrl,this.state.repoName,this.state.repoId);
                    this.setState({
                        open: false,
                        branchName:"",
                        versionId:"",
                        productId:"",
                        versionList:[],
                        branchList:[],
                        repoUrl:"",
                        repoName:"",
                        repoId:""
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
        axios.post(getServer() + '/lts/branches/changeVersion', data
        ).then(
            (response) => {
                let datat = response.data;
                this.props.fetchBranches(this.state.repoUrl,this.state.repoName,this.state.repoId);
                this.setState({
                    open: false,
                    branchName:"",
                    versionId:"",
                    productId:"",
                    versionList:[],
                    branchList:[],
                    repoUrl:"",
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
                            <AppBar className={classes.appBar} position="static" color="default">
                                <Toolbar>
                                    <Typography variant="title" color="inherit"  className={classes.flex}>
                                        Select Version
                                    </Typography>
                                    <IconButton className={classes.closeButton} color="inherit" onClick={this.handleClose} aria-label="Close">
                                        <CloseIcon/>
                                    </IconButton>
                                </Toolbar>
                            </AppBar>
                            <FormControl className={classes.inputSelect}>
                                <InputLabel htmlFor="age-native-simple">Version</InputLabel>
                                <Select
                                    value={this.state.versionId}
                                    onChange={this.handleChange("versionId")}
                                    input={<Input id='age-native-simple'/>}

                                >
                                    <MenuItem value="">
                                        <em>None</em>
                                    </MenuItem>
                                    {this.state.versionList.map((value,index)=>
                                        <MenuItem value={value["versionId"]} key={index}>{value["versionName"]}</MenuItem>
                                    )}
                                </Select>
                            </FormControl>
                            <FormControl className={classes.buttonList}>
                                <Button color="secondary" onClick={callbackFunc}>{buttonLabel}</Button>
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