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


import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {withStyles} from 'material-ui/styles';
import '../App.css';
import Grid from "material-ui/es/Grid/Grid";
import Paper from "material-ui/es/Paper/Paper";
import List from "material-ui/es/List/List";
import AppBar from "material-ui/es/AppBar/AppBar";
import Typography from "material-ui/es/Typography/Typography";
import Toolbar from "material-ui/es/Toolbar/Toolbar";
import BranchTable from "./product-repository/BranchTable.js";
import ProductNavigatorRepo from "./product-repository/ProductNavigatorRepo";
import {getServer} from "../resources/util";
import axios from "axios/index";
import RepoNameItem from "./product-repository/RepoNameItem";
import ChangeBranchVersionModal from "./product-repository/ChangeBranchVersionModal";


const styles = theme => ({
    root: {
        flexGrow: 1,
    },
    paper: {
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
    productButtons: {
        width: `100%`,
        paddingTop: '20px',
        backgroundColor: theme.palette.background.paper,
    },
    flex: {
        flex: 1,
    },
    productAppBar : {
        width: '100%',
    },
    appBar : {
        paddingBottom : `20px`
    },
    repoList : {
        paddingLeft: `5%`,
        paddingRight: `5%`
    }
});



class ProductRepository extends Component {

    constructor(props) {
        super(props);
        this.state = {
            currentRepo: "",
            currentRepoId:"",
            productId : "",
            repoList:[],
            branchList:[],
            branchVersionWindowOpen:false,
            branchVersionChnageData:{}

        };

        this.setRepoList = this.setRepoList.bind(this);
        this.fetchBranches = this.fetchBranches.bind(this);
        this.openBranchVersionAddWindow = this.openBranchVersionAddWindow.bind(this);

    }


    // open branch add / change window
    openBranchVersionAddWindow(data){
        this.setState({
            branchVersionWindowOpen:true,
            branchVersionChnageData:data
        })
    }

    // set repo list for product
    setRepoList(id){
        this.setState({
            repoList: [],
            branchList:[],
            productId:id,
            branchVersionWindowOpen:false
        });
        if(id!="") {
            let data = {
                productId: id
            };
            axios.post('http://' + getServer() + '/lts/products/repos', data
            ).then(
                (response) => {
                    let datat = response.data;
                    this.setState(
                        {
                            repoList: datat,
                            branchVersionWindowOpen:false
                        }
                    );
                }
            )
        }else {
            this.setState({
                repoList: [],
                branchVersionWindowOpen:false
            })
        }
    }


    // fetch branches for repo
    fetchBranches(repoId,repoName){
        console.log(repoId,repoName);
        this.setState(
            {
                branchList: [],
                currentRepo: repoName,
                currentRepoId: repoId,
                branchVersionWindowOpen:false

            }
        );

        let data = {
            repoId : repoId,
            repoName : repoName
        };

        axios.post('http://' + getServer() + '/lts/products/repos/branches', data
        ).then(
            (response) => {
                let datat = response.data;
                this.setState(
                    {
                        branchList: datat,
                        branchVersionWindowOpen:false
                    }
                );
            }
        )
    }


    render() {
        const {classes} = this.props;
        return (

            <div className={classes.root}>
                <Grid container spacing={24}>
                    <Grid item xs={3}>
                        <Paper className={classes.paper}>
                            <div className={classes.appBar}>
                                <AppBar position="static" color="default">
                                    <ProductNavigatorRepo
                                        setRepoList={this.setRepoList}
                                    />
                                </AppBar>
                            </div>
                            <div className={classes.productButtons}>
                                <List className={classes.repoList} component="nav">
                                    {this.state.repoList.map((value,index)=>
                                        <RepoNameItem
                                            repoObject={value}
                                            key={index}
                                            fetchBranches={this.fetchBranches}
                                        />
                                    )}
                                </List>
                            </div>
                        </Paper>
                    </Grid>

                    <Grid item xs={9}>
                        <Paper className={classes.paper}>
                            <div className={classes.appBar}>
                                <AppBar position="static" color="default">
                                    <Toolbar>
                                        <Typography type="title" color="inherit" className={classes.flex}>
                                            {this.state.currentRepo}
                                        </Typography>
                                    </Toolbar>
                                </AppBar>
                            </div>
                            <div>
                                <BranchTable
                                    branchList={this.state.branchList}
                                    openBranchVersionAddWindow = {this.openBranchVersionAddWindow}
                                />
                            </div>
                        </Paper>
                    </Grid>
                </Grid>
                <ChangeBranchVersionModal
                    open={this.state.branchVersionWindowOpen}
                    data={this.state.branchVersionChnageData}
                    repoId = {this.state.currentRepoId}
                    repoName={this.state.currentRepo}
                    fetchBranches={this.fetchBranches}
                    productId={this.state.productId}
                    branchList={this.state.branchList}
                />
            </div>



        );
    }
}

ProductRepository.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(ProductRepository);
