locals {
  common_tags = {
    "kubernetes.io/cluster/${var.cluster_id_tag}" = "${var.cluster_id_value}"
  }
}

#############################
# Adjustable variables
#############################

variable "number_of_controller" {
  description = "The number of controller, only acts as controller"
  default     = 0
}

variable "number_of_etcd" {
  description = "The number of etcd, only acts as etcd"
  default     = 0
}

variable "number_of_controller_etcd" {
  description = "The number of nodes which act as controller and etcd simultaneously"
  default     = 1
}

variable "number_of_worker" {
  description = "The number of worker nodes"
  default     = 0
}

variable "cluster_id_tag" {
  description = "Cluster ID tag for kubespray"
  default     = "kkt3289"
}

variable "cluster_id_value" {
  description = "Cluster ID value, it can be shared or owned"
  default     = "owned"
}

##########################
# Default variables (you can change for customizing)
##########################

variable "control_cidr" {
  description = "CIDR for maintenance: inbound traffic will be allowed from this IPs"
  default     = "0.0.0.0/0"
}

locals {
  default_keypair_public_key = file("../keys/tf-kube.pub")
}

/*
## It triggers interpolation. It is recommended to use another way.
## TODO : Replace default_keypair_public_key as output?
variable default_keypair_public_key {
  description = "Public Key of the default keypair"
  default = "${file("../keys/tf-kube.pub")}"
}
*/

variable "default_keypair_name" {
  description = "Name of the KeyPair used for all nodes"
  default     = "tf-kube"
}


variable "vpc_name" {
  description = "Name of the VPC"
  default     = "kubernetes"
}

variable "elb_name" {
  description = "Name of the ELB for Kubernetes API"
  default     = "kubernetes"
}

variable "owner" {
  default = "kkt3289"
}

variable "ansibleFilter" {
  description = "`ansibleFilter` tag value added to all instances, to enable instance filtering in Ansible dynamic inventory"
  default     = "Kubernetes01" # IF YOU CHANGE THIS YOU HAVE TO CHANGE instance_filters = tag:ansibleFilter=Kubernetes01 in ./ansible/hosts/ec2.ini
}

# Networking setup
variable "region" {
  default = "ap-northeast-2"
}

variable "zone" {
  default = "ap-northeast-2a"
}

### VARIABLES BELOW MUST NOT BE CHANGED ###

variable "vpc_cidr" {
  default = "10.43.0.0/16"
}

variable "kubernetes_pod_cidr" {
  default = "10.200.0.0/16"
}


# Instances Setup
variable "amis" {
  description = "Default AMIs to use for nodes depending on the region"
  type        = map(string)
  default = {
    # Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
    ap-northeast-2 = "ami-05a7f3469a7653972"
    ap-northeast-1 = "ami-0c1907b6d738188e5"
    ap-southeast-1 = "ami-0a2e29e3b4fc39212"
    eu-central-1   = "ami-04a5bacc58328233d"
    eu-west-1      = "ami-0f0c3baa60262d5b9"
    sa-east-1      = "ami-0975628249d7e8952"
    us-east-1      = "ami-0f9de6e2d2f067fca"
    us-west-1      = "ami-05e1c8b4e753b29d3"
    us-west-2      = "ami-03f8acd418785369b"
  }
}

variable "default_instance_user" {
  default = "ubuntu"
}

variable "etcd_instance_type" {
  default = "t3.xlarge"
}
variable "controller_instance_type" {
  default = "t3.xlarge"
}
variable "worker_instance_type" {
  default = "t2.small"
}


variable "kubernetes_cluster_dns" {
  default = "10.31.0.1"
}