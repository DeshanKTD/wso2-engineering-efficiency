import React from 'react';
import PropTypes from 'prop-types';
import { withStyles } from 'material-ui/styles';
import ListSubheader from 'material-ui/List/ListSubheader';
import List from 'material-ui/List';
import CalendarYear from '../navigatorcomponenets/CalendarYear'
import {getAllDates} from "../support/datepickers";
import Paper from 'material-ui/Paper';

const styles = theme => ({
    root: {
        width: 400,
        maxWidth: 600,
        background: theme.palette.background.paper,
    },
    nested: {
        paddingLeft: theme.spacing.unit * 4,
    },

    paper: theme.mixins.gutters({
        paddingLeft: 16,
        paddingTop: 16,
        paddingBottom: 16,
        marginTop: theme.spacing.unit * 3,
    }),
});

class WeekNavigator extends React.Component {
    constructor(props){
        super(props);
        this.state = {
            calendarDates : {}
        };
        this.setCalendarDates(this.props.pureData);
        this.setCheckDate = this.setCheckDate.bind(this);
    }


    setCalendarDates(data){
        this.setState (
            {
                calendarDates : getAllDates(data)
            }
        );
    }


    componentWillUpdate(nextProps,nextState){
        if(nextProps.pureData!==undefined) {
            if(this.props != nextProps) {
                this.setCalendarDates(nextProps.pureData);
            }
        }

    }

    setCheckDate(sDate,eDate){
        this.props.setDate(sDate,eDate);
    }


    renderButtons(){
        if(Object.keys(this.state.calendarDates).length>0) {
            return Object.keys(this.state.calendarDates).map((years,index) => (
                <CalendarYear
                    key={index}
                    year={years}
                    yearData={this.state.calendarDates[years]}
                    setDate={this.setCheckDate}
                />
            ))
        }
    }

    render() {
        const { classes } = this.props;

        return (
            <Paper className={classes.root} elevation={5}>
                <List className={classes.root} subheader={<ListSubheader>Week Navigation</ListSubheader>}>
                    {
                        this.renderButtons()
                    }
                </List>
            </Paper>
        );
    }
}

WeekNavigator.propTypes = {
    classes: PropTypes.object.isRequired,
};

export default withStyles(styles)(WeekNavigator);