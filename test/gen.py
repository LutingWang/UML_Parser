import sys
import re

# 0. CLASS_COUNT
# 1. CLASS_OPERATION_COUNT          classname operation_query_type
# 2. CLASS_ATTR_COUNT               classname attribute_query_type
# 3. CLASS_ASSO_COUNT               classname
# 4. CLASS_ASSO_CLASS_LIST          classname
# 5. CLASS_OPERATION_VISIBILITY     classname methodname
# 6. CLASS_ATTR_VISIBILITY          classname attrname
# 7. CLASS_TOP_BASE                 classname
# 8. CLASS_IMPLEMENT_INTERFACE_LIST classname
# 9. CLASS_INFO_HIDDEN              classname

def gen_class_commands(classes, methods, attrs):
    commands = ["CLASS_COUNT", "CLASS_OPERATION_COUNT", "CLASS_ATTR_COUNT",
                "CLASS_ASSO_COUNT", "CLASS_ASSO_CLASS_LIST", "CLASS_OPERATION_VISIBILITY",
                "CLASS_ATTR_VISIBILITY", "CLASS_TOP_BASE", "CLASS_IMPLEMENT_INTERFACE_LIST",
                "CLASS_INFO_HIDDEN"]
    operation_query_types = ["NON_RETURN", "RETURN", "NON_PARAM", "PARAM", "ALL"]
    attribute_query_type = ["ALL", "SELF_ONLY"]
    yield commands[0]
    for i in range(1, 10):
        for classname in classes:
            command = commands[i] + " " + classname
            if i == 1:
                for oqt in operation_query_types:
                    yield command + " " + oqt
            elif i == 2:
                for aqt in attribute_query_type:
                    yield command + " " + aqt
            elif i == 5:
                for m in methods:
                    yield command + " " + m
            elif i == 6:
                for a in attrs:
                    yield command + " " + a
            else:
                yield command

def gen_state_machine_commands(state_machines, states):
    commands = ["STATE_COUNT", "TRANSITION_COUNT", "SUBSEQUENT_STATE_COUNT"]
    for i in range(3):
        for state_machine_name in state_machines:
            command = commands[i] + " " + state_machine_name
            if i == 2:
                for state_name in states:
                    yield command + " " + state_name
            else:
                yield command

def gen_interaction_commands(interactions, lifelines):
    commands = ["PTCP_OBJ_COUNT", "MESSAGE_COUNT", "INCOMING_MSG_COUNT"]
    for i in range(3):
        for interaction_name in interactions:
            command = commands[i] + " " + interaction_name
            if i == 2:
                for lifeline_name in lifelines:
                    yield command + " " + lifeline_name
            else:
                yield command


if __name__ == '__main__':
    class_pattern = re.compile('"name":"([^"]*)","_type":"UMLClass"')
    method_pattern = re.compile('"name":"([^"]*)","_type":"UMLOperation"')
    attr_pattern = re.compile('"name":"([^"]*)","_type":"UMLAttribute"')
    state_machine_pattern = re.compile('"name":"([^"]*)","_type":"UMLStateMachine"')
    state_pattern = re.compile('"name":"([^"]*)","_type":"UMLState"')
    interaction_pattern = re.compile('"name":"([^"]*)","_type":"UMLInteraction"')
    lifeline_pattern = re.compile('"name":"([^"]*)","_type":"UMLLifeline"')
    classes = []
    methods = []
    attrs = []
    state_machines = []
    states = []
    interactions = []
    lifelines = []
    filename = sys.argv[1]
    with open(filename, 'r') as f:
        for e in f.readlines():
            classes += class_pattern.findall(e)
            methods += method_pattern.findall(e)
            attrs += attr_pattern.findall(e)
            state_machines += state_machine_pattern.findall(e)
            states += state_pattern.findall(e)
            interactions += interaction_pattern.findall(e)
            lifelines += lifeline_pattern.findall(e)
    classes = list(set(classes)) + ["random_class_2019"]
    methods = list(set(methods)) + ["random_method_2019"]
    attrs = list(set(attrs)) + ["random_attr_2019"]
    state_machines = list(set(state_machines)) + ["random_state_machine_2019"]
    states = list(set(states)) + ["random_state_2019"]
    interactions = list(set(interactions)) + ["random_interaction_2019"]
    lifelines = list(set(lifelines)) + ["random_lifeline_2019"]

    with open(filename, 'a') as f:
        for command in gen_class_commands(classes, methods, attrs):
            f.write(command + "\n")
        for command in gen_state_machine_commands(state_machines, states):
            f.write(command + "\n")
        for command in gen_interaction_commands(interactions, lifelines):
            f.write(command + "\n")