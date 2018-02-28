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
import VersionTable from "./product-version/VersionTable.js";
import Button from "material-ui/es/Button/Button";
import axios from "axios/index";
import {getServer} from "../resources/util";
import ProductNameItem from "./product-version/ProductNameItem";
import VersionAddModal from "./product-version/VersionAddModal";
import VersionDeleteDialog from "./product-version/VersionDeleteDialog";
import VersionChangeModal from "./product-version/VersionChangeModal";


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
    productList : {
        paddingLeft: `5%`,
        paddingRight: `5%`
    }
});



class ProductVersion extends Component {

    constructor(props) {
        super(props);
        this.state = {
            productList : [],
            addProductOpen : false,
            productName : "",
            productId : "",
            versionList: [],
            deleteData: {},
            deleteProductOpen: false,
            changeVersionOpen: false
        };
        this.fetchProductList();
        this.fetchVersionList = this.fetchVersionList.bind(this);
        this.setName = this.setName.bind(this);
        this.openAddProductWindow = this.openAddProductWindow.bind(this);
        this.openDeleteWindow = this.openDeleteWindow.bind(this);
        this.openChangeWindow = this.openChangeWindow.bind(this);

    }

    fetchProductList() {
        axios.get('http://'+getServer()+'/lts/products/names'
        ).then(
            (response) => {
                let datat = response.data;
                this.setState(
                    {
                        productList: datat,
                        addProductOpen : false,
                        deleteProductOpen: false,
                        changeVersionOpen: false,
                    }
                );
            }
        )
    }


    fetchVersionList(productId){
        let data = {
            productId : productId
        };
        axios.post('http://'+getServer()+'/lts/products/versions',data
        ).then(
            (response) => {
                let datat = response.data;
                this.setState(
                    {
                        versionList: datat,
                        addProductOpen : false,
                        deleteProductOpen : false,
                        changeVersionOpen:false
                    },
                );
            }
        )
    }

    setName(productName,productId){
        this.setState({
            productName : productName,
            productId : productId,
            addProductOpen : false,
            deleteProductOpen: false,
            changeVersionOpen:false
        })
    }


    openAddProductWindow(){
        this.setState({
            addProductOpen : true,
            deleteProductOpen: false,
            changeVersionOpen:false
        })
    }


    openDeleteWindow(versionId,versionName){
        let data = {
            productId: this.state.productId,
            versionId: versionId,
            versionName: versionName
        };
        this.setState({
            deleteData: data,
            deleteProductOpen: true,
            addProductOpen: false,
            changeVersionOpen:false

        });
    }


    openChangeWindow(versionId,versionName){
        let data = {
            productId: this.state.productId,
            versionId: versionId,
            versionName: versionName,
        };
        this.setState({
            changeData: data,
            deleteProductOpen: false,
            addProductOpen: false,
            changeVersionOpen:true
        });
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
                                    <Toolbar>
                                        <Typography type="title"
                                                    color="inherit"
                                                    className={classes.flex}
                                                    component="h2"
                                        >
                                            Products
                                        </Typography>
                                    </Toolbar>
                                </AppBar>
                            </div>
                            <div className={classes.productButtons}>
                                <List component="nav" className={classes.productList}>
                                    {
                                        this.state.productList.map((value,index)=>
                                                <ProductNameItem
                                                    productObject={value}
                                                    key={index}
                                                    getVersionList ={this.fetchVersionList}
                                                    setName={this.setName}
                                                />
                                        )
                                    }
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
                                            {this.state.productName}
                                        </Typography>
                                        <Button raised onClick={this.openAddProductWindow}>
                                            Add Version
                                        </Button>
                                    </Toolbar>
                                </AppBar>
                            </div>
                            <div>
                                <VersionTable
                                    tableData = {this.state.versionList}
                                    openDeleteWindow={this.openDeleteWindow}
                                    openChangeWindow={this.openChangeWindow}
                                />
                            </div>
                        </Paper>
                    </Grid>
                </Grid>
                <VersionAddModal
                    open = {this.state.addProductOpen}
                    productId = {this.state.productId}
                    versionList = {this.state.versionList}
                    fetchVersions = {this.fetchVersionList}

                />
                <VersionDeleteDialog
                    open = {this.state.deleteProductOpen}
                    deleteData = {this.state.deleteData}
                    fetchVersions = {this.fetchVersionList}
                    productId={this.state.productId}
                />
                <VersionChangeModal
                    open = {this.state.changeVersionOpen}
                    data = {this.state.changeData}
                    versionList = {this.state.versionList}
                    fetchVersions = {this.fetchVersionList}
                />
            </div>

        );
    }
}

ProductVersion.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(ProductVersion);
