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
import ListItem from "material-ui/es/List/ListItem";
import ListItemIcon from "material-ui/es/List/ListItemIcon";
import ListItemText from "material-ui/es/List/ListItemText";
import StarIcon from 'material-ui-icons/Star';
import AppBar from "material-ui/es/AppBar/AppBar";
import Typography from "material-ui/es/Typography/Typography";
import Toolbar from "material-ui/es/Toolbar/Toolbar";
import BranchTable from "./product-repository/BranchTable.js";
import ProductNavigatorRepo from "./product-repository/ProductNavigatorRepo";
import {getServer} from "../resources/util";
import axios from "axios/index";
import RepoNameItem from "./product-repository/RepoNameItem";


const styles = theme => ({
    root: {
        flexGrow: 1,
        marginTop: 30,
    },
    paper: {
        padding: 16,
        textAlign: 'center',
        color: theme.palette.text.secondary,
    },
    productButtons: {
        width: `100%`,
        maxWidth: 360,
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
    }
});



class ProductRepository extends Component {

    constructor(props) {
        super(props);
        this.state = {
            currentRepo: "",
            productId : "",
            repoList:[],
            branchList:[],

        };

        this.setRepoList = this.setRepoList.bind(this);

    }



    // set repo list for product
    setRepoList(id){
        this.setState({
            repoList: []
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
                            repoList: datat
                        },()=>console.log(this.state.repoList)
                    );
                }
            )
        }else {
            this.setState({
                repoList: []
            })
        }
    }


    // fetch branches for repo
    fetchBranches(){

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
                                <List component="nav">
                                    {this.state.repoList.map((value,index)=>
                                        <RepoNameItem repoObject={value} key={index}/>
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
                                            product-ei
                                        </Typography>
                                    </Toolbar>
                                </AppBar>
                            </div>
                            <div>
                                <BranchTable
                                    branchList={this.state.branchList}
                                />
                            </div>
                        </Paper>
                    </Grid>
                </Grid>
            </div>



        );
    }
}

ProductRepository.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(ProductRepository);
