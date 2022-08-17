package world.hzq.ioc.impl;

public class GenericBeanDefinition extends AbstractBeanDefinition {
    @Override
    public boolean equals(Object obj) {
        if(this == obj){
            return true;
        }
        if(!(obj instanceof GenericBeanDefinition)){
            return false;
        }
        GenericBeanDefinition gbd = (GenericBeanDefinition) obj;
        return this.getBeanName().equals(gbd.getBeanName()) && this.getBeanClassName().equals(gbd.getBeanClassName()) && this.getBeanClass() == gbd.getBeanClass();
    }

    @Override
    public String toString() {
        return "this bean simple name is " + getBeanName() + ",fully qualified class name is + " + getBeanClassName();
    }
}
