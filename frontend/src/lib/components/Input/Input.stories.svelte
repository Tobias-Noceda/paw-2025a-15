<script module lang="ts">
  import { defineMeta } from '@storybook/addon-svelte-csf';
  import Input from './Input.svelte';
  import { fn } from 'storybook/test';

  const { Story } = defineMeta({
    title: 'lib/Input',
    component: Input,
    tags: ['autodocs'],
    argTypes: {
      label: { control: 'text' },
      placeholder: { control: 'text' },
      type: { control: { type: 'select', options: ['text', 'password', 'number', 'email'] } },
      errorMessage: { control: 'text' },
      disabled: { control: 'boolean' },
      required: { control: 'boolean' },
      class: { control: 'text' },
      oninput: { action: 'input changed' }
    }
  });
</script>

<Story name="Default" args={{ label: 'Username', placeholder: 'Enter your username', type: 'text' }} />

<Story name="With Error" args={{ label: 'Number', placeholder: 'Enter a number', type: 'number', errorMessage: 'This field is required' }} />

<Story name="Required" args={{ label: 'Password', placeholder: 'Enter password', type: 'password', required: true }} />

<Story name="Skeleton" args={{ label: 'Username', placeholder: 'Loading...', skeleton: true }} />

<Story name="Disabled" args={{ label: 'Username', placeholder: 'Cannot edit', disabled: true }} />
